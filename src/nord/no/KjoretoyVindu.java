package nord.no;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.Color;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JScrollPane;

public class KjoretoyVindu extends JFrame implements ItemListener, ListSelectionListener, ActionListener  {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private List<Kjoretoy> kListe = new ArrayList<Kjoretoy>();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_5;
	private JTextField textField_4;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_3;
	private JList<Kjoretoy> liste;
	private JComboBox<String> typeKj;
	private String dbName = "jdbc:derby://localhost:1527/vehicle_data_286101;create=true";
	Connection dbConnection = null;
	Statement sqlSetning = null;
	PreparedStatement prepSetning = null;
	DatabaseMetaData dbMeta = null;
	ResultSet dbRS = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KjoretoyVindu frame = new KjoretoyVindu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public KjoretoyVindu() {
		setTitle("Kj�ret�y liste");
		try {
			dbConnection = DriverManager.getConnection(dbName);
			sqlSetning = dbConnection.createStatement();
			dbMeta = dbConnection.getMetaData();
			dbRS = dbMeta.getTables(null, "APP", "KJORETOY", null);
			if (!dbRS.next()) {
				String createKjoretoy = "create table kjoretoy (id integer not null, type varchar(20) not null, type_id varchar(1) not null, regnr varchar(10) not null, chassisnr varchar(40) primary key, "+
						"ant_hk integer, toppfart integer, ant_passasjer integer, ant_hjul integer, tank integer, forbruk double)";
				Boolean sqlSvar = sqlSetning.execute(createKjoretoy);
			} else {
				System.out.println("tabellen kjoretoy finnes allerede");
			}
			dbRS.close();
			dbRS = dbMeta.getTables(null, "APP", "KJORETOY_MED_DOR", null);
			if (!dbRS.next()) {
				String createKjoretoyMedDor = "create table kjoretoy_med_dor (id integer not null, chassisnr varchar(40), ant_dorer integer, foreign key(chassisnr) references kjoretoy(chassisnr))";
					//	+ "type varchar(20) not null, type_id integer not null, regnr varchar(10) not null, chassisnr varchar(40) not null, "+
					//	"ant_hk integer, toppfart integer, ant_passasjer integer, ant_hjul integer, ant_dorer integer, tank integer, forbruk double)";
			Boolean sqlSvar = sqlSetning.execute(createKjoretoyMedDor);
			} else {
				System.out.println("tabellen kjoretoy_med_dor finnes allerede");
			}
			dbRS.close();
			String sqlKjoretoy = "insert into kjoretoy (id, type, type_id, regnr, chassisnr, ant_hk, toppfart, ant_passasjer, ant_hjul, tank, forbruk) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			String sqlKjoretoyMedDor = "insert into kjoretoy_med_dor (id, chassisnr, ant_dorer) values (?, ?, ?)";
			PreparedStatement prepKjoretoy = dbConnection.prepareStatement(sqlKjoretoy);
			PreparedStatement prepKjoretoyMedDor = dbConnection.prepareStatement(sqlKjoretoyMedDor);
			int kjoretoyId = 1;
			loadFromDB();
	    	if (kListe.size() == 0) {
				innFilen();
				System.out.println("Du kom hit");
				for (Kjoretoy k : kListe) {			
					prepKjoretoy.setInt(1, kjoretoyId);
					prepKjoretoy.setString(2, k.getVehicleName());
					prepKjoretoy.setInt(3, k.getTypenr());
					prepKjoretoy.setString(4, k.getRegNumber());
					prepKjoretoy.setString(5, k.getChassiNumber());
					prepKjoretoy.setInt(6, k.getEngineSize());
					prepKjoretoy.setInt(7, k.getTopSpeed());
					prepKjoretoy.setInt(8, k.getNumPassengers());
					prepKjoretoy.setInt(9, k.getWheels());
					prepKjoretoy.setInt(10, k.getTankVolume());
					prepKjoretoy.setDouble(11, k.getPerMil());
					prepKjoretoy.execute();
					if (k instanceof KjoretoyMedDor) {
						prepKjoretoyMedDor.setInt(1, kjoretoyId);
						prepKjoretoyMedDor.setString(2, k.getChassiNumber());
						prepKjoretoyMedDor.setInt(3, ((KjoretoyMedDor) k).getNumDoors());
						prepKjoretoyMedDor.execute();
					}
					kjoretoyId++;
				}
			}
		}
		catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DefaultListModel<Kjoretoy> listeModel = new DefaultListModel<Kjoretoy>();
		for (Kjoretoy k : kListe) {
			listeModel.addElement(k);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 785, 605);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {76, 124, 208, 138, 208};
		gbl_contentPane.rowHeights = new int[] {344, 40, 40, 40, 40, 40};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		contentPane.setLayout(gbl_contentPane);
						
		JTextArea txtrKjretyListe = new JTextArea();
		txtrKjretyListe.setBackground(Color.LIGHT_GRAY);
		txtrKjretyListe.setFont(new Font("Dialog", Font.BOLD, 18));
		txtrKjretyListe.setText("Kjøretøy liste");
		txtrKjretyListe.setEditable(false);
		GridBagConstraints gbc_txtrKjretyListe = new GridBagConstraints();
		gbc_txtrKjretyListe.gridwidth = 2;
		gbc_txtrKjretyListe.insets = new Insets(0, 0, 5, 5);
		gbc_txtrKjretyListe.gridx = 0;
		gbc_txtrKjretyListe.gridy = 0;
		contentPane.add(txtrKjretyListe, gbc_txtrKjretyListe);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		liste = new JList<Kjoretoy>();
		scrollPane.setViewportView(liste);
		liste.addListSelectionListener(this);
		liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		liste.setModel(listeModel);
		liste.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				try{
				e.getSource();
				Kjoretoy k = getListe().getSelectedValue();
				int ind = Character.getNumericValue(k.getVehicleType());
				getTypeKj().setSelectedIndex(ind);
				if (getTypeKj().getSelectedIndex() <=(6)) {					
					KjoretoyMedDor kmd = (KjoretoyMedDor) getListe().getSelectedValue();
					getAntDor().setEnabled(true);
					getRegNum().setText(kmd.getRegNumber());
					getChassisNr().setText(kmd.getChassiNumber());
					getHK().setText(Integer.toString(kmd.getEngineSize()));
					getMaxFart().setText(Integer.toString(kmd.getTopSpeed()));
					getAntPassasjer().setText(Integer.toString(kmd.getNumPassengers()));
					getAntHjul().setText(Integer.toString(kmd.getWheels()));
					getTank().setText(Integer.toString(kmd.getTankVolume()));
					getForbruk().setText(Double.toString(kmd.getPerMil()));
					getAntDor().setText((Integer.toString(kmd.getNumDoors())));			
				}
				else if (getTypeKj().getSelectedIndex()>=(7)) {
					getAntDor().setText("");
					getRegNum().setText(k.getRegNumber());
					getChassisNr().setText(k.getChassiNumber());
					getHK().setText(Integer.toString(k.getEngineSize()));
					getMaxFart().setText(Integer.toString(k.getTopSpeed()));
					getAntPassasjer().setText(Integer.toString(k.getNumPassengers()));
					getAntHjul().setText(Integer.toString(k.getWheels()));
					getTank().setText(Integer.toString(k.getTankVolume()));
					getForbruk().setText(Double.toString(k.getPerMil()));
					}
				}
				catch (NullPointerException ex){
		//			JOptionPane.showMessageDialog(null, ex);					
				}
			}	
		});		
		JButton btnLeggTillKjrety = new JButton("Nytt kjøretøy");
		btnLeggTillKjrety.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					LeggTilKjVindu nyttVindu = new LeggTilKjVindu();
					nyttVindu.setVisible(true);
					dispose();
				}			
		});
		GridBagConstraints gbc_btnLeggTillKjrety = new GridBagConstraints();
		gbc_btnLeggTillKjrety.insets = new Insets(0, 0, 5, 5);
		gbc_btnLeggTillKjrety.gridx = 0;
		gbc_btnLeggTillKjrety.gridy = 1;
		contentPane.add(btnLeggTillKjrety, gbc_btnLeggTillKjrety);
		
		JButton btnEndreKjrety = new JButton("Endre kjøretøy");
		btnEndreKjrety.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (getListe().getSelectedIndex() == -1) {
						JOptionPane.showMessageDialog(null, "Ingen kjøretøy valgt!");										
					}
					else if (getListe().getSelectedIndex() >= 0){				
					//	e.getSource();
						boolean sjekk = true;
						Kjoretoy k = getListe().getSelectedValue();			
						if (getTypeKj().getSelectedIndex() <=(6)){		
							getAntDor().setEnabled(true);
							int test = getListe().getSelectedIndex();
							int funnet = 0;		
							for (Kjoretoy kfinn : getKListe()){							
								String regnr = kfinn.getRegNumber();
								if (getRegNum().getText().equalsIgnoreCase(regnr) &  funnet != test) {
									JOptionPane.showMessageDialog(null, "Du har forsøkt å endre RegNr. til et nummer som innehas av et annet kjøretøy!");
									sjekk = false;
									break;
								}
								funnet++;	
							}
							if (sjekk== true){
								KjoretoyMedDor kmd = (KjoretoyMedDor) getListe().getSelectedValue();							
								kmd.setRegNumber(getRegNum().getText());
								kmd.setEngineSize(Integer.parseInt(getHK().getText()));
								kmd.setTopSpeed(Integer.parseInt(getMaxFart().getText()));
								kmd.setNumPassengers(Integer.parseInt(getAntPassasjer().getText()));
								kmd.setTankVolume(Integer.parseInt(getTank().getText()));
								kmd.setPerMil((Double.parseDouble(getForbruk().getText())));
							}							
						}
						else if (getTypeKj().getSelectedIndex() >=(7)){
							getAntDor().setEnabled(false);
							int test = getListe().getSelectedIndex();
							int funnet = 0;		
							for (Kjoretoy kfinn : getKListe()){
								String regnr = kfinn.getRegNumber();												
								if (getRegNum().getText().equalsIgnoreCase(regnr) &  funnet != test)  {
									JOptionPane.showMessageDialog(null, "Du har forsøkt å endre RegNr. til et nummer som innehas av et annet kjøretøy!");
									sjekk = false;
									break;
								}
								funnet++;
							}
							if (sjekk== true){
								k.setRegNumber(getRegNum().getText());
								k.setEngineSize(Integer.parseInt(getHK().getText()));
								k.setTopSpeed(Integer.parseInt(getMaxFart().getText()));
								k.setNumPassengers(Integer.parseInt(getAntPassasjer().getText()));
								k.setTankVolume(Integer.parseInt(getTank().getText()));
								k.setPerMil((Double.parseDouble(getForbruk().getText())));													
							}							
						}
						if (sjekk == true){
							DefaultListModel<Kjoretoy> temp = new DefaultListModel<Kjoretoy>();
							for (Kjoretoy endrekj: kListe) {
								temp.addElement(endrekj);					
							}				
							getListe().setModel(temp);
							saveToDB();				
							JOptionPane.showMessageDialog(null, "Kjøretøyet er endret!");	
						}					
					}
				}
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Du har feil verdier på et/eller flere felter.");
				}		
			}
		});
		GridBagConstraints gbc_btnEndreKjrety = new GridBagConstraints();
		gbc_btnEndreKjrety.insets = new Insets(0, 0, 5, 5);
		gbc_btnEndreKjrety.gridx = 0;
		gbc_btnEndreKjrety.gridy = 2;
		contentPane.add(btnEndreKjrety, gbc_btnEndreKjrety);
		
		JTextArea txtrTypeKjrety = new JTextArea();
		txtrTypeKjrety.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrTypeKjrety.setBackground(Color.LIGHT_GRAY);
		txtrTypeKjrety.setText("Type kjøretøy");
		txtrTypeKjrety.setEditable(false);
		GridBagConstraints gbc_txtrTypeKjrety = new GridBagConstraints();
		gbc_txtrTypeKjrety.insets = new Insets(0, 0, 5, 5);
		gbc_txtrTypeKjrety.gridx = 1;
		gbc_txtrTypeKjrety.gridy = 1;
		contentPane.add(txtrTypeKjrety, gbc_txtrTypeKjrety);
		
	//	String[] kjType={"", "Personbil", "Varebil", "Buss", "Lastebil", "Trailer", "Traktor", "Motorsykkel", "Moped"};
		typeKj = new JComboBox<String>();
		typeKj.setModel(new DefaultComboBoxModel<String>(new String[] {"", "Personbil", "Varebil", "Buss", "Lastebil", "Trailer", "Traktor", "Motorsykkel", "Moped"}));
		typeKj.addItemListener(this);
		typeKj.setEnabled(true);
		typeKj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				if (e.getSource() == typeKj) {
					String meld = (String) typeKj.getSelectedItem();
					switch (meld) {			
						case "":						
							JOptionPane.showMessageDialog(null, "Kjøretøy type må velges! ");	
							break;						
						case "Personbil": 
							getAntDor().setEnabled(true);
							break;	
						case "Varebil":
							getAntDor().setEnabled(true);
							break;	
						case "Buss":
							getAntDor().setEnabled(true);
							break;
						case "Lastebil":
							getAntDor().setEnabled(true);
							break;	
						case "Trailer":
							getAntDor().setEnabled(true);
							break;
						case "Traktor":
							getAntDor().setEnabled(true);
							break;	
						case "Motorsykkel":
							getAntDor().setEnabled(false);
							break;
						case "Moped":
							getAntDor().setEnabled(false);
							break;											
					}					
				}
			}			
		});
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 1;
		contentPane.add(typeKj, gbc_comboBox);
		
		JTextArea txtrPassasjerMfrer = new JTextArea();
		txtrPassasjerMfrer.setText("Passasjerer m/fører");
		txtrPassasjerMfrer.setEditable(false);
		txtrPassasjerMfrer.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrPassasjerMfrer.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_txtrPassasjerMfrer = new GridBagConstraints();
		gbc_txtrPassasjerMfrer.insets = new Insets(0, 0, 5, 5);
		gbc_txtrPassasjerMfrer.gridx = 3;
		gbc_txtrPassasjerMfrer.gridy = 1;
		contentPane.add(txtrPassasjerMfrer, gbc_txtrPassasjerMfrer);
		
		textField_4 = new JTextField();
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.insets = new Insets(0, 0, 5, 0);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 4;
		gbc_textField_4.gridy = 1;
		contentPane.add(textField_4, gbc_textField_4);
		textField_4.setColumns(10);
				
		JTextArea txtrRegistreringsnr = new JTextArea();
		txtrRegistreringsnr.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrRegistreringsnr.setText("Registreringsnr.");
		txtrRegistreringsnr.setEditable(false);
		txtrRegistreringsnr.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_txtrRegistreringsnr = new GridBagConstraints();
		gbc_txtrRegistreringsnr.insets = new Insets(0, 0, 5, 5);
		gbc_txtrRegistreringsnr.gridx = 1;
		gbc_txtrRegistreringsnr.gridy = 2;
		contentPane.add(txtrRegistreringsnr, gbc_txtrRegistreringsnr);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 2;
		contentPane.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JTextArea txtrAntallHjul = new JTextArea();
		txtrAntallHjul.setText("Antall hjul");
		txtrAntallHjul.setEditable(false);
		txtrAntallHjul.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrAntallHjul.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_txtrAntallHjul = new GridBagConstraints();
		gbc_txtrAntallHjul.insets = new Insets(0, 0, 5, 5);
		gbc_txtrAntallHjul.gridx = 3;
		gbc_txtrAntallHjul.gridy = 2;
		contentPane.add(txtrAntallHjul, gbc_txtrAntallHjul);
		
		textField_5 = new JTextField();
		GridBagConstraints hjul = new GridBagConstraints();
		hjul.insets = new Insets(0, 0, 5, 0);
		hjul.fill = GridBagConstraints.HORIZONTAL;
		hjul.gridx = 4;
		hjul.gridy = 2;
		contentPane.add(textField_5, hjul);
		textField_5.setColumns(10);
		
		JButton btnVisRekkevidde = new JButton("Vis Rekkevidde");
		btnVisRekkevidde.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (getListe().getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(null, "Ingen kjøretøy valgt!");										
				}
				else {
					Kjoretoy k = getListe().getSelectedValue();
					JOptionPane.showMessageDialog(null, (Double.toString(k.getRekkeVidde()) + " mil, per tank"));
				}
			}
		});
		GridBagConstraints gbc_btnVisRekkevidde = new GridBagConstraints();
		gbc_btnVisRekkevidde.insets = new Insets(0, 0, 5, 5);
		gbc_btnVisRekkevidde.gridx = 0;
		gbc_btnVisRekkevidde.gridy = 3;
		contentPane.add(btnVisRekkevidde, gbc_btnVisRekkevidde);
		
		JTextArea txtrChassisnummer = new JTextArea();
		txtrChassisnummer.setBackground(Color.LIGHT_GRAY);
		txtrChassisnummer.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrChassisnummer.setText("Chassisnr.");
		txtrChassisnummer.setEditable(false);
		GridBagConstraints gbc_txtrChassisnummer = new GridBagConstraints();
		gbc_txtrChassisnummer.insets = new Insets(0, 0, 5, 5);
		gbc_txtrChassisnummer.gridx = 1;
		gbc_txtrChassisnummer.gridy = 3;
		contentPane.add(txtrChassisnummer, gbc_txtrChassisnummer);
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 3;
		contentPane.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		JTextArea txtrAntallDrer = new JTextArea();
		txtrAntallDrer.setText("Antall dører");
		txtrAntallDrer.setEditable(false);
		txtrAntallDrer.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrAntallDrer.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_txtrAntallDrer = new GridBagConstraints();
		gbc_txtrAntallDrer.insets = new Insets(0, 0, 5, 5);
		gbc_txtrAntallDrer.gridx = 3;
		gbc_txtrAntallDrer.gridy = 3;
		contentPane.add(txtrAntallDrer, gbc_txtrAntallDrer);
		
		textField_6 = new JTextField();
		textField_6.setEnabled(true);
		GridBagConstraints gbc_textField_6 = new GridBagConstraints();
		gbc_textField_6.insets = new Insets(0, 0, 5, 0);
		gbc_textField_6.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_6.gridx = 4;
		gbc_textField_6.gridy = 3;
		contentPane.add(textField_6, gbc_textField_6);
		textField_6.setColumns(10);
		
		JTextArea txtrMotorstrrelse = new JTextArea();
		txtrMotorstrrelse.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrMotorstrrelse.setText("Motorstørrelse");
		txtrMotorstrrelse.setBackground(Color.LIGHT_GRAY);
		txtrMotorstrrelse.setEditable(false);
		GridBagConstraints gbc_txtrMotorstrrelse = new GridBagConstraints();
		gbc_txtrMotorstrrelse.insets = new Insets(0, 0, 5, 5);
		gbc_txtrMotorstrrelse.gridx = 1;
		gbc_txtrMotorstrrelse.gridy = 4;
		contentPane.add(txtrMotorstrrelse, gbc_txtrMotorstrrelse);
		
		textField_2 = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 2;
		gbc_textField_2.gridy = 4;
		contentPane.add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);
		
		JTextArea txtrTank = new JTextArea();
		txtrTank.setText("Tank");
		txtrTank.setEditable(false);
		txtrTank.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrTank.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_txtrTank = new GridBagConstraints();
		gbc_txtrTank.insets = new Insets(0, 0, 5, 5);
		gbc_txtrTank.gridx = 3;
		gbc_txtrTank.gridy = 4;
		contentPane.add(txtrTank, gbc_txtrTank);
		
		textField_7 = new JTextField();
		GridBagConstraints gbc_textField_7 = new GridBagConstraints();
		gbc_textField_7.insets = new Insets(0, 0, 5, 0);
		gbc_textField_7.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_7.gridx = 4;
		gbc_textField_7.gridy = 4;
		contentPane.add(textField_7, gbc_textField_7);
		textField_7.setColumns(10);
		
		JButton btnSlettKjrety = new JButton("Slett kjøretøy");
		btnSlettKjrety.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String sqlSlettKjoretoy = "delete from kjoretoy where id=?";
					String sqlSlettKjoretoyMedDor = "delete from kjoretoy_med_dor where id=?";
					String sqlID = "select id from kjoretoy";
					PreparedStatement prepSlettKjoretoy = dbConnection.prepareStatement(sqlSlettKjoretoy);
					PreparedStatement prepSlettKjoretoyMedDor = dbConnection.prepareStatement(sqlSlettKjoretoyMedDor);
					int tilSletting = getListe().getSelectedValue().getIdDB();
					if (getListe().getSelectedValue() instanceof KjoretoyMedDor) {
						
						prepSlettKjoretoyMedDor.setInt(1, tilSletting);
						prepSlettKjoretoy.setInt(1, tilSletting);
						prepSlettKjoretoyMedDor.execute();
						prepSlettKjoretoy.execute();
					}
					else {
						prepSlettKjoretoy.setInt(1, tilSletting);
						prepSlettKjoretoy.execute();
					}
					getKListe().remove(getListe().getSelectedIndex());
					DefaultListModel<Kjoretoy> temp = new DefaultListModel<Kjoretoy>();
					for (Kjoretoy k: getKListe()) {
						temp.addElement(k);
					}
					getListe().setModel(temp);
					//utFilen();
					saveToDB();
				}
				catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		});
		GridBagConstraints gbc_btnSlettKjrety = new GridBagConstraints();
		gbc_btnSlettKjrety.insets = new Insets(0, 0, 0, 5);
		gbc_btnSlettKjrety.gridx = 0;
		gbc_btnSlettKjrety.gridy = 5;
		contentPane.add(btnSlettKjrety, gbc_btnSlettKjrety);
		
		JTextArea txtrToppfart = new JTextArea();
		txtrToppfart.setText("Maxfart");
		txtrToppfart.setEditable(false);
		txtrToppfart.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrToppfart.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_txtrToppfart = new GridBagConstraints();
		gbc_txtrToppfart.insets = new Insets(0, 0, 0, 5);
		gbc_txtrToppfart.gridx = 1;
		gbc_txtrToppfart.gridy = 5;
		contentPane.add(txtrToppfart, gbc_txtrToppfart);
		
		textField_3 = new JTextField();
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.insets = new Insets(0, 0, 0, 5);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 2;
		gbc_textField_3.gridy = 5;
		contentPane.add(textField_3, gbc_textField_3);
		textField_3.setColumns(10);
		
		JTextArea txtrForbruk = new JTextArea();
		txtrForbruk.setText("Forbruk pr. mil");
		txtrForbruk.setEditable(false);
		txtrForbruk.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrForbruk.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_txtrForbruk = new GridBagConstraints();
		gbc_txtrForbruk.insets = new Insets(0, 0, 0, 5);
		gbc_txtrForbruk.gridx = 3;
		gbc_txtrForbruk.gridy = 5;
		contentPane.add(txtrForbruk, gbc_txtrForbruk);
		
		textField_8 = new JTextField();
		GridBagConstraints gbc_textField_8 = new GridBagConstraints();
		gbc_textField_8.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_8.gridx = 4;
		gbc_textField_8.gridy = 5;
		contentPane.add(textField_8, gbc_textField_8);
		textField_8.setColumns(10);
	}
	private void loadFromDB() {
		String sqlLastKjoretoy = "select * from kjoretoy";
		String sqlKjoretoyMedDor = "select * from kjoretoy_med_dor where chassisnr=?";
		String sqlDorer = ("select ant_dorer from kjoretoy_med_dor where chassisnr=?");
		ResultSet kjoretoymeddor = null;
		ResultSet dorer = null;
		try {
			PreparedStatement prepKjoretoyMedDor = dbConnection.prepareStatement(sqlKjoretoyMedDor);
			PreparedStatement prepDorer = dbConnection.prepareStatement(sqlDorer);
			dbRS = sqlSetning.executeQuery(sqlLastKjoretoy);
			while (dbRS.next()) {
				prepKjoretoyMedDor.setString(1, dbRS.getString(5));
				kjoretoymeddor = prepKjoretoyMedDor.executeQuery();
				kjoretoymeddor.next();
				prepDorer.setString(1, dbRS.getString(5));
				dorer = prepDorer.executeQuery();
				dorer.next();
				if (dbRS.getInt(3) <= 6)  {
					Kjoretoy tempdor = new KjoretoyMedDor(Integer.toString(dbRS.getInt(3)).charAt(0), dbRS.getString(5), dbRS.getInt(9), dorer.getInt(1));
					tempdor.setIdDB(dbRS.getInt(1));
					tempdor.setRegNumber(dbRS.getString(4));
					tempdor.setEngineSize(dbRS.getInt(6));
					tempdor.setTopSpeed(dbRS.getInt(7));
					tempdor.setNumPassengers(dbRS.getInt(8));
					tempdor.setTankVolume(dbRS.getInt(10));
					tempdor.setPerMil(dbRS.getDouble(11));
					kListe.add(tempdor);					
				}
				else{
					Kjoretoy temp = new Kjoretoy(Integer.toString(dbRS.getInt(3)).charAt(0), dbRS.getString(5), dbRS.getInt(9));
					temp.setIdDB(dbRS.getInt(1));
					temp.setRegNumber(dbRS.getString(4));
					temp.setEngineSize(dbRS.getInt(6));
					temp.setTopSpeed(dbRS.getInt(7));
					temp.setNumPassengers(dbRS.getInt(8));
					temp.setTankVolume(dbRS.getInt(10));
					temp.setPerMil(dbRS.getDouble(11));
					kListe.add(temp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void innFilen() {
		String filNavn = "kjøretøylisten.txt";
		try {	//Denne biten er kopiert fra consolmeny klassen på moodle
			kListe = new ArrayList<Kjoretoy>();
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filNavn));
			Object obj = null;
			while ((obj = ois.readObject()) != null) {
				if (obj instanceof Kjoretoy) { 														
				kListe.add((Kjoretoy) obj);
				}
			}
			ois.close();
			}
		catch (FileNotFoundException noFileException) {
			System.out.println(noFileException.getMessage());
			}
		catch (IOException inputException) {

			} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	public void saveToDB() {
		String sqlKjoretoy = "select * from kjoretoy where id=?";
		String sqlUpdateKjoretoy = "update kjoretoy set regnr=?, ant_hk=?, toppfart=?, ant_passasjer=?, ant_hjul=?,tank=?, forbruk=? where id=?";
		String sqlUpdateKjoretoyMedDor= "update kjoretoy_med_dor set ant_dorer=? where id=?";
		try {
			PreparedStatement prepKjoretoy = dbConnection.prepareStatement(sqlKjoretoy);
			PreparedStatement updateKjoretoy = dbConnection.prepareStatement(sqlUpdateKjoretoy);
			PreparedStatement updateKjoretoyMedDor = dbConnection.prepareStatement(sqlUpdateKjoretoyMedDor);
			String insertKjoretoy = "insert into kjoretoy (id, type, type_id, regnr, chassisnr, ant_hk, toppfart, ant_passasjer, ant_hjul, tank, forbruk) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			String insertKjoretoyMedDor = "insert into kjoretoy_med_dor (id, chassisnr, ant_dorer) values (?, ?, ?)";
			PreparedStatement prepInsertKjoretoy = dbConnection.prepareStatement(insertKjoretoy);
			PreparedStatement prepInsertKjoretoyMedDor = dbConnection.prepareStatement(insertKjoretoyMedDor);
			ResultSet rsKjoretoy = null;
			for (Kjoretoy k: kListe) {
				prepKjoretoy.setInt(1, k.getIdDB());
				rsKjoretoy = prepKjoretoy.executeQuery();
				if (rsKjoretoy.next()) {
					updateKjoretoy.setString(1, k.getRegNumber());
					updateKjoretoy.setInt(2, k.getEngineSize());
					updateKjoretoy.setInt(3, k.getTopSpeed());
					updateKjoretoy.setInt(4, k.getNumPassengers());
					updateKjoretoy.setInt(5, k.getWheels());
					updateKjoretoy.setInt(6, k.getTankVolume());
					updateKjoretoy.setDouble(7, k.getPerMil());
					updateKjoretoy.setInt(8, k.getIdDB());
					updateKjoretoy.execute();
					if (k instanceof KjoretoyMedDor) {
					//	updateKjoretoyMedDor.setInt(1, k.getIdDB());
						updateKjoretoyMedDor.setInt(1, ((KjoretoyMedDor)k).getNumDoors());
						updateKjoretoyMedDor.setInt(2, k.getIdDB());
						updateKjoretoyMedDor.execute();
					}
				} 
				else {
					prepInsertKjoretoy.setInt(1, k.getIdDB());
					prepInsertKjoretoy.setString(2, k.getVehicleName());
					prepInsertKjoretoy.setInt(3, k.getTypenr());
					prepInsertKjoretoy.setString(4, k.getRegNumber());
					prepInsertKjoretoy.setString(5, k.getChassiNumber());
					prepInsertKjoretoy.setInt(6, k.getEngineSize());
					prepInsertKjoretoy.setInt(7, k.getTopSpeed());
					prepInsertKjoretoy.setInt(8, k.getNumPassengers());
					prepInsertKjoretoy.setInt(9, k.getWheels());
					prepInsertKjoretoy.setInt(10, k.getTankVolume());
					prepInsertKjoretoy.setDouble(11, k.getPerMil());
					prepInsertKjoretoy.execute();
					if (k instanceof KjoretoyMedDor) {
						prepInsertKjoretoyMedDor.setInt(1, k.getIdDB());
						prepInsertKjoretoyMedDor.setString(2, k.getChassiNumber());
						prepInsertKjoretoyMedDor.setInt(3, ((KjoretoyMedDor) k).getNumDoors());
						prepInsertKjoretoyMedDor.execute();				
					}
				}
			}
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void utFilen() {
		String filNavn = "kjøretøylisten.txt";
		try { 	//Denne biten er kopiert fra consolmeny klassen på moodle
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filNavn));
			for (Kjoretoy k : kListe) {
				oos.writeObject(k);
				}
			oos.close();
			} 
		catch (FileNotFoundException notFound) { 
			System.out.println(notFound.getMessage());
			} 
		catch (IOException outputException) {
			System.out.println(outputException.getMessage());
			}
	}
	private void getDbId() {
		try {
		String sqlIdKj = "select id from kjoretoy";
		PreparedStatement prepID = dbConnection.prepareStatement(sqlIdKj);
		ResultSet rsIden = null;
		rsIden = prepID.executeQuery();
		rsIden.next();
		return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public JList<Kjoretoy> getListe(){
		return liste;
	}
	public List<Kjoretoy> getKListe() {
		return kListe;
	}
	public JTextField getRegNum() {
		return textField;
	}
	public JTextField getChassisNr() {
		return textField_1;
	}
	public JTextField getHK() {
		return textField_2;
	}
	public JTextField getMaxFart() {
		return textField_3;
	}
	public JTextField getAntPassasjer() {
		return textField_4;
	}
	public JTextField getAntHjul() {
		return textField_5;
	}
	public JTextField getTank() {
		return textField_7;
	}
	public JTextField getAntDor() {
		return textField_6;
	}
	public JTextField getForbruk() {
		return textField_8;
	}
	public JComboBox<String> getTypeKj() {
		return typeKj;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
	
	}	
}
