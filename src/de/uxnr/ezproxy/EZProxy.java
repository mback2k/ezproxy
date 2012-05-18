package de.uxnr.ezproxy;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.uxnr.proxy.Proxy;

public class EZProxy {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// Nothing to do here
		}

		try {
			EZProxySession session = new EZProxySession();
			session.begin();

			EZProxyRewriter rewriter = new EZProxyRewriter();
			rewriter.setSession(session);

			Proxy proxy = new Proxy(session.getPort());
			proxy.addHostRewriter(".*", rewriter);
			proxy.start();

			JOptionPane.showMessageDialog(null, "Proxy is running on localhost:" + session.getPort(), "Proxy", JOptionPane.INFORMATION_MESSAGE);
			EZProxy.tray(proxy, session);
		} catch (Error e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void tray(final Proxy proxy, final EZProxySession session) throws AWTException {
		final SystemTray tray = SystemTray.getSystemTray();
		final ImageIcon icon = new ImageIcon(EZProxy.class.getResource("EZProxy.png"));
		final TrayIcon trayIcon = new TrayIcon(icon.getImage(), "EZProxy");
		final MenuItem exitItem = new MenuItem("Exit");
		final PopupMenu popup = new PopupMenu();

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				proxy.stop();
				try {
					session.end();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
				}
				tray.remove(trayIcon);
				System.exit(0);
			}
		});

		popup.add(exitItem);
		trayIcon.setPopupMenu(popup);
		tray.add(trayIcon);
	}
}
