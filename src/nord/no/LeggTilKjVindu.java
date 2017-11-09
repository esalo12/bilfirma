package nord.no;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class LeggTilKjVindu extends JFrame implements ItemListener, ListSelectionListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static List<Kjoretoy> kListe = new ArrayList<Kjoretoy>();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
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
					LeggTilKjVindu frame = new LeggTilKjVindu();
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
	public LeggTilKjVindu() {
		setTitle("Nytt kj�ret�y");
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
				String createKjoretoyMedDor = "create table kjoretoy_med_dor (id integer not null, chassisnr varchar(40), ant_dorer integer)";
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
			loadFromDB();
			if (kListe.size() == 0) {
				innFilen();
				System.out.println("Du kom hit");
				int kjoretoyId = 1;
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
						prepKjoretoyMedDor.setInt(2, ((KjoretoyMedDor) k).getNumDoors());
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 795, 633);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {196, 277, 248};
		gbl_contentPane.rowHeights = new int[] {28, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 50};
		gbl_contentPane.columnWeights = new double[]{1.0};
		gbl_contentPane.rowWeights = new double[]{Double.MIN_VALUE, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		contentPane.setLayout(gbl_contentPane);
		
		JTextArea txtrTypeKjrety = new JTextArea();
		txtrTypeKjrety.setBackground(Color.LIGHT_GRAY);
		txtrTypeKjrety.setFont(new Font("Arial Black", Font.PLAIN, 13));
		txtrTypeKjrety.setText("Type kjøretøy");
		txtrTypeKjrety.setEditable(false);
		GridBagConstraints gbc_txtrTypeKjrety = new GridBagConstraints();
		gbc_txtrTypeKjrety.insets = new Insets(0, 0, 5, 5);
		gbc_txtrTypeKjrety.gridx = 0;
		gbc_txtrTypeKjrety.gridy = 1;
		contentPane.add(txtrTypeKjrety, gbc_txtrTypeKjrety);
		
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
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		contentPane.add(typeKj, gbc_comboBox);
		
		JTextArea txtrRegistreringsnummer = new JTextArea();
		txtrRegistreringsnummer.setBackground(Color.LIGHT_GRAY);
		txtrRegistreringsnummer.setText("Registreringsnr.");
		txtrRegistreringsnummer.setEditable(false);
		txtrRegistreringsnummer.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrRegistreringsnummer = new GridBagConstraints();
		gbc_txtrRegistreringsnummer.insets = new Insets(0, 0, 5, 5);
		gbc_txtrRegistreringsnummer.gridx = 0;
		gbc_txtrRegistreringsnummer.gridy = 2;
		contentPane.add(txtrRegistreringsnummer, gbc_txtrRegistreringsnummer);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 2;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 2;
		contentPane.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JTextArea txtrChassisnr = new JTextArea();
		txtrChassisnr.setBackground(Color.LIGHT_GRAY);
		txtrChassisnr.setText("Chassisnr.");
		txtrChassisnr.setEditable(false);
		txtrChassisnr.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrChassisnr = new GridBagConstraints();
		gbc_txtrChassisnr.insets = new Insets(0, 0, 5, 5);
		gbc_txtrChassisnr.gridx = 0;
		gbc_txtrChassisnr.gridy = 3;
		contentPane.add(txtrChassisnr, gbc_txtrChassisnr);
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 2;
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 3;
		contentPane.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		JTextArea txtrMotorstrrelse = new JTextArea();
		txtrMotorstrrelse.setBackground(Color.LIGHT_GRAY);
		txtrMotorstrrelse.setText("Motorstørrelse");
		txtrMotorstrrelse.setEditable(false);
		txtrMotorstrrelse.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrMotorstrrelse = new GridBagConstraints();
		gbc_txtrMotorstrrelse.insets = new Insets(0, 0, 5, 5);
		gbc_txtrMotorstrrelse.gridx = 0;
		gbc_txtrMotorstrrelse.gridy = 4;
		contentPane.add(txtrMotorstrrelse, gbc_txtrMotorstrrelse);
		
		textField_2 = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.gridwidth = 2;
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 4;
		contentPane.add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);
		
		JTextArea txtrMaxfart = new JTextArea();
		txtrMaxfart.setBackground(Color.LIGHT_GRAY);
		txtrMaxfart.setText("Maxfart");
		txtrMaxfart.setEditable(false);
		txtrMaxfart.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrMaxfart = new GridBagConstraints();
		gbc_txtrMaxfart.insets = new Insets(0, 0, 5, 5);
		gbc_txtrMaxfart.gridx = 0;
		gbc_txtrMaxfart.gridy = 5;
		contentPane.add(txtrMaxfart, gbc_txtrMaxfart);
		
		textField_3 = new JTextField();
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.gridwidth = 2;
		gbc_textField_3.insets = new Insets(0, 0, 5, 0);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 1;
		gbc_textField_3.gridy = 5;
		contentPane.add(textField_3, gbc_textField_3);
		textField_3.setColumns(10);
		
		JTextArea txtrPaasasjererMfrer = new JTextArea();
		txtrPaasasjererMfrer.setBackground(Color.LIGHT_GRAY);
		txtrPaasasjererMfrer.setText("Passasjerer m/fører");
		txtrPaasasjererMfrer.setEditable(false);
		txtrPaasasjererMfrer.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrPaasasjererMfrer = new GridBagConstraints();
		gbc_txtrPaasasjererMfrer.insets = new Insets(0, 0, 5, 5);
		gbc_txtrPaasasjererMfrer.gridx = 0;
		gbc_txtrPaasasjererMfrer.gridy = 6;
		contentPane.add(txtrPaasasjererMfrer, gbc_txtrPaasasjererMfrer);
		
		textField_4 = new JTextField();
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.gridwidth = 2;
		gbc_textField_4.insets = new Insets(0, 0, 5, 0);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 1;
		gbc_textField_4.gridy = 6;
		contentPane.add(textField_4, gbc_textField_4);
		textField_4.setColumns(10);
		
		JTextArea txtrAntallHjul = new JTextArea();
		txtrAntallHjul.setBackground(Color.LIGHT_GRAY);
		txtrAntallHjul.setText("Antall hjul");
		txtrAntallHjul.setEditable(false);
		txtrAntallHjul.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrAntallHjul = new GridBagConstraints();
		gbc_txtrAntallHjul.insets = new Insets(0, 0, 5, 5);
		gbc_txtrAntallHjul.gridx = 0;
		gbc_txtrAntallHjul.gridy = 7;
		contentPane.add(txtrAntallHjul, gbc_txtrAntallHjul);
		
		textField_5 = new JTextField();
		GridBagConstraints gbc_textField_5 = new GridBagConstraints();
		gbc_textField_5.gridwidth = 2;
		gbc_textField_5.insets = new Insets(0, 0, 5, 0);
		gbc_textField_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_5.gridx = 1;
		gbc_textField_5.gridy = 7;
		contentPane.add(textField_5, gbc_textField_5);
		textField_5.setColumns(10);
		
		JTextArea txtrTank = new JTextArea();	
		txtrTank.setBackground(Color.LIGHT_GRAY);
		txtrTank.setText("Tank");
		txtrTank.setEditable(false);
		txtrTank.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrTank = new GridBagConstraints();
		gbc_txtrTank.insets = new Insets(0, 0, 5, 5);
		gbc_txtrTank.gridx = 0;
		gbc_txtrTank.gridy = 8;
		contentPane.add(txtrTank, gbc_txtrTank);
		
		textField_6 = new JTextField();
		GridBagConstraints gbc_textField_6 = new GridBagConstraints();
		gbc_textField_6.gridwidth = 2;
		gbc_textField_6.insets = new Insets(0, 0, 5, 0);
		gbc_textField_6.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_6.gridx = 1;
		gbc_textField_6.gridy = 8;
		contentPane.add(textField_6, gbc_textField_6);
		textField_6.setColumns(10);
		
		JTextArea txtrAntallDrer = new JTextArea();
		txtrAntallDrer.setEditable(false);
		txtrAntallDrer.setBackground(Color.LIGHT_GRAY);
		txtrAntallDrer.setText("Antall dører");
		txtrAntallDrer.setEditable(false);
		txtrAntallDrer.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrAntallDrer = new GridBagConstraints();
		gbc_txtrAntallDrer.insets = new Insets(0, 0, 5, 5);
		gbc_txtrAntallDrer.gridx = 0;
		gbc_txtrAntallDrer.gridy = 9;
		contentPane.add(txtrAntallDrer, gbc_txtrAntallDrer);
		
		textField_7 = new JTextField();
		GridBagConstraints gbc_textField_7 = new GridBagConstraints();
		gbc_textField_7.gridwidth = 2;
		gbc_textField_7.insets = new Insets(0, 0, 5, 0);
		gbc_textField_7.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_7.gridx = 1;
		gbc_textField_7.gridy = 9;
		contentPane.add(textField_7, gbc_textField_7);
		textField_7.setColumns(10);
		
		JTextArea txtrForbruk = new JTextArea();
		txtrForbruk.setBackground(Color.LIGHT_GRAY);
		txtrForbruk.setText("Forbruk");
		txtrForbruk.setEditable(false);
		txtrForbruk.setFont(new Font("Arial Black", Font.PLAIN, 13));
		GridBagConstraints gbc_txtrForbruk = new GridBagConstraints();
		gbc_txtrForbruk.insets = new Insets(0, 0, 5, 5);
		gbc_txtrForbruk.gridx = 0;
		gbc_txtrForbruk.gridy = 10;
		contentPane.add(txtrForbruk, gbc_txtrForbruk);
		
		textField_8 = new JTextField();
		GridBagConstraints gbc_textField_8 = new GridBagConstraints();
		gbc_textField_8.gridwidth = 2;
		gbc_textField_8.insets = new Insets(0, 0, 5, 0);
		gbc_textField_8.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_8.gridx = 1;
		gbc_textField_8.gridy = 10;
		contentPane.add(textField_8, gbc_textField_8);
		textField_8.setColumns(10);
		
		JButton btnLagre = new JButton("Lagre");		
		btnLagre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (typeKj.getSelectedIndex()== 0){
						JOptionPane.showMessageDialog(null, "Kjøretøy type må velges! ");						
					}	
					else{
						char kjtypen = Integer.toString(typeKj.getSelectedIndex()).charAt(0);
						String regNum = getRegNum();
						String chassis = getChassisNr();
						int hk = Integer.parseInt(getHK().getText());
						int maxf = Integer.parseInt(getMaxFart().getText());
						int passasjer =Integer.parseInt(getAntPassasjer().getText());
						int anthjul = Integer.parseInt(getAntHjul().getText());
						int tank = Integer.parseInt(getTank().getText());
						double forbruk = Double.parseDouble(getForbruk().getText());
						boolean foundRegNo = false;
						String sqlNesteId = "select max(id) from kjoretoy";
						PreparedStatement prepNesteId = dbConnection.prepareStatement(sqlNesteId);
						ResultSet nesteId = null;
						nesteId = prepNesteId.executeQuery();
						nesteId.next();
						int nyId = nesteId.getInt(1);
						System.out.println(nyId);
						for (Kjoretoy oldVehicle : kListe) {
								if (oldVehicle.getRegNumber().equalsIgnoreCase(regNum) || oldVehicle.getChassiNumber().equalsIgnoreCase(chassis)) {
									JOptionPane.showMessageDialog(null, "Kjøretøyet finnes allerede!");
									foundRegNo = true;
									break;
								}
						}
						if (foundRegNo==false){
							if (typeKj.getSelectedIndex()==(7) || typeKj.getSelectedIndex()==(8)){
								Kjoretoy mc = new Kjoretoy(kjtypen, chassis, anthjul);
								mc.setIdDB(nyId+(1));
								mc.setRegNumber(regNum);
								mc.setEngineSize(hk);			
								mc.setTopSpeed(maxf);
								mc.setNumPassengers(passasjer);
								mc.setTankVolume(tank);
								mc.setPerMil(forbruk);
								kListe.add(mc);
								saveToDB();
								JOptionPane.showMessageDialog(null, "Kjøretøyet er lagret!");
							}
							else{
								int antdor = Integer.parseInt(getAntDor().getText());
								Kjoretoy bil = new KjoretoyMedDor(kjtypen, chassis, anthjul, antdor);
								bil.setIdDB(nyId+(1));
								bil.setRegNumber(regNum);
								bil.setEngineSize(hk);
								bil.setTopSpeed(maxf);
								bil.setNumPassengers(passasjer);
								bil.setTankVolume(tank);
								bil.setPerMil(forbruk);
								kListe.add(bil);
								saveToDB();
								JOptionPane.showMessageDialog(null, "Kjøretøyet er lagret!");
							}							
						}
					}
				}
				catch ( NumberFormatException | SQLException ne ) {
					System.out.println(ne);
					JOptionPane.showMessageDialog(null, "Du har feil verdier på et/eller flere felter.");
					}		
			}		
		});
		GridBagConstraints gbc_btnLagre = new GridBagConstraints();
		gbc_btnLagre.insets = new Insets(0, 0, 0, 5);
		gbc_btnLagre.gridx = 1;
		gbc_btnLagre.gridy = 11;
		contentPane.add(btnLagre, gbc_btnLagre);
		
		JButton btnNewButton = new JButton("Avbryt");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KjoretoyVindu tilbake = new KjoretoyVindu();
				tilbake.setVisible(true);
				dispose();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 11;
		contentPane.add(btnNewButton, gbc_btnNewButton);
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
		try {			
			kListe = new ArrayList<Kjoretoy>();
			// les inn objekter fra fil vha en fileinputstream
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filNavn));
			Object obj = null;
			while ((obj = ois.readObject()) != null) {
				if (obj instanceof Kjoretoy) { // Sjekk at innlest objekt er at korrekt type															
				kListe.add((Kjoretoy) obj);
				}
			}
			ois.close();
			}
		catch (FileNotFoundException noFileException) {
			// Fil finnes ikke skriv ut feilmelding og fortsett
			System.out.println(noFileException.getMessage());
			}
		catch (IOException inputException) {
			// Denne oppst�r ved ikke flere objekter i fila
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
		try { 				
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filNavn));
			for (Kjoretoy k : kListe) {
				oos.writeObject(k);
				}
			oos.close();
			} 
		catch (FileNotFoundException notFound) { 
			// Dersom fil ikke kan opprettes skriv ut feilmelding og fortsett
			System.out.println(notFound.getMessage());
			} 
		catch (IOException outputException) {
			System.out.println(outputException.getMessage());
			}
	}
	public List<Kjoretoy> getListe(){
		return kListe;
	}
	public JComboBox<String> getTypeKj() {
		return typeKj;
	}
	public String getRegNum() {
		return textField.getText().toUpperCase();
	}
	public String getChassisNr() {
		return textField_1.getText();
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
		return textField_6;
	}
	public JTextField getAntDor() {
		return textField_7;
	}
	public JTextField getForbruk() {
		return textField_8;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub		
	}
}
