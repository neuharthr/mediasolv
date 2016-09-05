/*
 * Main.java
 *
 * Created on 20 mai 2006, 01:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingbeanformbuilder.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.services.FormBuilder;
import swingbeanformbuilder.gui.ISBFBForm;

/**
 * A simple demo of SBFB, it shows how to integrate SBFB generated forms
 * in a standard Swing application.
 * 
 * @author Simon OUALID
 */
public class Main {

	public static void main(String[] args) {
		// set platform default l&f
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// load the SBFB's configuration  
		SBFBConfiguration.loadConfiguration("sbfb-demo-configuration.xml");
		final JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(new BorderLayout());
		f.setTitle("SBFB 0.1 - Demo Application");

		final ISBFBForm form = FormBuilder.buildForm(DemoBean.class, f);

		// Load data in the SBFBForm... 
		DemoBean b = createDummyData();
		form.loadData(b);		
		
		// You can easily set the form (un)editable
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		final JButton editable = new JButton("Set uneditable");
		editable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				form.setEditable(!form.isEditable());
				if (form.isEditable()) {
					editable.setText("Set uneditable");
				} else {
					editable.setText("Set editable");
				}
			}
		});
		panel.add(Box.createHorizontalGlue());
		panel.add(editable);
		
		// and it's also easy to read what user typed		
		JButton read = new JButton("Read user input");
		read.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuffer sb = new StringBuffer();
				Map m = form.readUserInput();
				Iterator it = m.keySet().iterator();
				while (it.hasNext()) {
					Object k = it.next();
					sb.append(k);
					sb.append(" = ");
					Object o = m.get(k);
					sb.append(o);
					sb.append("\n");
				}
				JOptionPane.showMessageDialog(f, sb.toString(), "user input",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panel.add(read);
		panel.add(Box.createHorizontalGlue());
		f.getContentPane().add(panel, BorderLayout.SOUTH);

		f.getContentPane().add((Component)form, BorderLayout.CENTER);
		
		f.pack();
		final Dimension minDimension = f.getSize();
		f.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				if (f.getSize().width < minDimension.width) {
					f.setSize(minDimension.width, f.getSize().height);
				}
				if (f.getSize().height < minDimension.height) {
					f.setSize(f.getSize().width, minDimension.height);
				}
			}
		});
		f.setVisible(true);
	}

	public static DemoBean createDummyData() {
		DemoBean b = new DemoBean();
		b.setLastname("Simpson");
		b.setFirstname("Homer");
		b.setSexe("M");
		b.setAge(44);
		b.setComments("Lorem ipsum dolor ...");
		b.setBirthday(new Date());
		DemoBean f = new DemoBean();
		f.setLastname("Simpson");
		f.setFirstname("Marge");
		f.setSexe("F");
		f.setAge(35);
		f.setComments("Marge!");
		f.getFriends().add(b);
		b.getFriends().add(f);
		f = new DemoBean();
		f.setLastname("Simpson");
		f.setFirstname("Bart");
		f.setSexe("M");
		f.setAge(11);
		f.setComments("Bart!");
		f.setDad(b);
		f.getFriends().add(b);
		b.getFriends().add(f);
		f = new DemoBean();
		f.setLastname("Simpson");
		f.setFirstname("Lisa");
		f.setSexe("F");
		f.setAge(9);
		f.setComments("Lisa!");
		f.getFriends().add(b);
		b.getFriends().add(f);
		f = new DemoBean();
		f.setLastname("Simpson");
		f.setFirstname("Grandpa");
		f.setSexe("M");
		f.setAge(98);
		f.setComments("Grandpa simpson!");
		b.setDad(f);
		return b;
	}
}
