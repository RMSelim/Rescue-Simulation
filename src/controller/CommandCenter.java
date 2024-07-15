package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.State;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.RepaintManager;

import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.disasters.Disaster;
import model.disasters.Infection;
import model.events.SOSListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;
import model.units.UnitState;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulator;
import view.GameOver;
import view.SimGUI;

public class CommandCenter implements SOSListener{

	private Simulator engine;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private SimGUI SimGUI;
	private int cycle=0;
	private JButton x[][]= new JButton[10][10];
	private GameOver gameOver;
	boolean flag= false;
	private Unit unit;
	private boolean respond=true;
	
	@SuppressWarnings("unused")
	private ArrayList<Unit> emergencyUnits;

	public CommandCenter() throws Exception {
		engine = new Simulator(this);
		SimGUI= new SimGUI();
		
		for (int i =0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				
				x[i][j]= new JButton();
				SimGUI.addgrid(x[i][j]);
			}
		}
		
		for(Unit u:engine.getEmergencyUnits()) {
			JButton b=new JButton();
			if(u instanceof Ambulance) {
				ImageIcon ambulance= new ImageIcon("amb.PNG");
				b.setIcon(ambulance);
			}
			if(u instanceof Evacuator) {
				ImageIcon evac= new ImageIcon("evac.PNG");
				b.setIcon(evac);
			}
			if(u instanceof GasControlUnit) {
				ImageIcon gas= new ImageIcon("gas.PNG");
				b.setIcon(gas);
			}
			if(u instanceof FireTruck) {
				ImageIcon fire= new ImageIcon("fire.PNG");
				b.setIcon(fire);
			}
			if(u instanceof DiseaseControlUnit) {
				ImageIcon disease= new ImageIcon("disease.PNG");
				b.setIcon(disease);
			}
			
			b.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					flag=true;
					unit=u;
					dispunit(u);
					
				}
			});
			SimGUI.addavunitbutton(b);
			
		}
		
		updategeneral();
		JButton cycle= new JButton("Next Cycle");
		cycle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(engine.checkGameOver()) {
					SimGUI.dispose();
					gameOver= new GameOver();
					gameOver.setVisible(true);
					gameOver.addcasualties(engine.calculateCasualties());
				}
				else {
					engine.nextCycle();
					updateunits();
					updategeneral();
					updateinfo();
					updatelog();
				}
				
			}
		});
		
		SimGUI.addnxtcyc(cycle);
		SimGUI.setVisible(true);
		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();

	}
	
	public void updatelog() {
		SimGUI.getLog1().removeAllElements();
		SimGUI.addlog("Active Disasters:");
		for(Disaster d: engine.getExecutedDisasters()) {
			if(d.isActive()) {
				if(d.getTarget() instanceof ResidentialBuilding) {
				String s=d.getClass().getSimpleName()+" disaster on Residential Building in cell "+d.getTarget().getLocation().toString();
				SimGUI.addlog(s);
				}
				else if(d.getTarget() instanceof Citizen) {
					String s=d.getClass().getSimpleName()+" disaster on Citizen in cell "+d.getTarget().getLocation().toString();
					SimGUI.addlog(s);
				}
			}
		}
		SimGUI.addlog("-----------------------------------------------------------");
		SimGUI.addlog("Events:");
		for(Citizen c:visibleCitizens) {
			if(c.getState()==CitizenState.DECEASED) {
				String s="The Citizen in cell "+c.getLocation().toString()+" has died.";
				SimGUI.addlog(s);
			}
		}
		for(ResidentialBuilding b: visibleBuildings) {
			if(b.getStructuralIntegrity()==0) {
				String s="The Residential Building in cell "+b.getLocation().toString()+" has collapsed and all it's occupants have died.";
				SimGUI.addlog(s);
			}
		}
		SimGUI.addlog("-----------------------------------------------------------");
		SimGUI.addlog("Executed Disasters:");
		for(Disaster d: engine.getExecutedDisasters()) {
			if(d.getTarget() instanceof ResidentialBuilding) {
				String s="A "+d.getClass().getSimpleName()+" disaster has struck the Residential Building in cell "+d.getTarget().getLocation().toString();
				SimGUI.addlog(s);
			}
			else if(d.getTarget() instanceof Citizen) {
				String s="A "+d.getClass().getSimpleName()+" disaster has struck the Citizen in cell "+d.getTarget().getLocation().toString();
				SimGUI.addlog(s);
			}
		}
	}
	
	public void updategeneral() {
		String general="Casualties: "+engine.calculateCasualties() + "\n\n"+ "Current Cycle: "+cycle;
		SimGUI.addgeneral(general);
		
		cycle++;
	}
	
	public void updateinfo() {

		for(ResidentialBuilding b: visibleBuildings) {
			int z=b.getLocation().getX();
			int y=b.getLocation().getY();
			ImageIcon building=new ImageIcon("building.png");
			x[z][y].setIcon(building);
			for(Unit u:emergencyUnits) {
				if(b.getLocation().equals(u.getLocation())) {
					if(u instanceof FireTruck) {
						if(b.getFireDamage()!=0) {
							ImageIcon bfire= new ImageIcon("b+fire.png");
							x[z][y].setIcon(bfire);
						}
						else {
							x[z][y].setIcon(building);
						}
					}
					if(u instanceof GasControlUnit) {
						if(b.getGasLevel()!=0) {
							ImageIcon bgas= new ImageIcon("b+gas.png");
							x[z][y].setIcon(bgas);
						}
						else {
							x[z][y].setIcon(building);
						}
					}
					if(u instanceof Evacuator) {
						if(b.getOccupants().size()!=0) {
							ImageIcon bevac= new ImageIcon("evac.png");
							x[z][y].setIcon(bevac);
						}
						else {
							x[z][y].setIcon(building);
						}
					}
				}
			}
			if(b.getStructuralIntegrity()==0) {
				ImageIcon collapsedbuilding= new ImageIcon("collapsedbuilding.png");
				x[z][y].setIcon(collapsedbuilding);
			}
			x[z][y].addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(flag==true) {
						try {
							unit.respond(b);
						} catch (IncompatibleTargetException e1) {
							respond=false;
							String message="This unit can only treat Citizens.";
							JOptionPane.showMessageDialog(null, message, "Wrong Target", 1);
						} catch (CannotTreatException e1) {
							respond=false;
							String message="This target is not in danger or the wrong unit might have been chosen to treat it.";
							JOptionPane.showMessageDialog(null, message, "Wrong Target", 1);
						}
					if(respond==true) {
						String message="This unit will now respond to the Residential Building in cell "+b.getLocation().toString();
						JOptionPane.showMessageDialog(null, message, "Confirming Respond", 1);
					}
					flag=false;	
					}
					else
						dispbuilding(b);

				}
			});
		}
		for(Citizen c:visibleCitizens) {
			int z=c.getLocation().getX();
			int y=c.getLocation().getY();
			ImageIcon citizen=new ImageIcon("citizen.png");
			x[z][y].setIcon(citizen);
			for(Unit u:emergencyUnits) {
				if(c.getLocation().equals(u.getLocation())) {
					if(u instanceof Ambulance) {
						if(c.getHp()!=0) {
							ImageIcon camb= new ImageIcon("c+amb.png");
							x[z][y].setIcon(camb);
						}
						else {
							x[z][y].setIcon(citizen);
						}
					}
					if(u instanceof DiseaseControlUnit) {
						if(c.getHp()!=0) {
							ImageIcon cinf= new ImageIcon("c+inf.png");
							x[z][y].setIcon(cinf);
						}
						else {
							x[z][y].setIcon(citizen);
						}
					}
				}
				
			}
			if(c.getState()==CitizenState.DECEASED) {
				ImageIcon deadcitizen= new ImageIcon("deadcitizen.png");
				x[z][y].setIcon(deadcitizen);
			}
			x[z][y].addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(flag==true) {
						try {
							unit.respond(c);
						} catch (IncompatibleTargetException e1) {
							respond=false;
							String message="This unit can only respond to Residential Buildings.";
							JOptionPane.showMessageDialog(null, message, "Wrong Target", 1);
						} catch (CannotTreatException e1) {
							respond=false;
							String message="This target is not in danger or the wrong unit might have been chosen to treat it.";
							JOptionPane.showMessageDialog(null, message, "Wrong Target", 1);
						}
					if(respond==true) {
						String message="This unit will now respond to the Residential Building in cell "+c.getLocation().toString();
						JOptionPane.showMessageDialog(null, message, "Confirming Respond", 1);
					}
					flag=false;	
					}
					else
						dispcitizen(c);
					
				}
			});
			
		}
	}
	public void updateunits() {
		SimGUI.getAvunits().removeAll();
		SimGUI.getRespunits().removeAll();
		SimGUI.repaint();
		SimGUI.validate();
		for(Unit u:engine.getEmergencyUnits()) {
			if (u.getState()==UnitState.IDLE){
				JButton b=new JButton();
				if(u instanceof Ambulance) {
					ImageIcon ambulance= new ImageIcon("amb.PNG");
					b.setIcon(ambulance);
				}
				if(u instanceof Evacuator) {
					ImageIcon evac= new ImageIcon("evac.PNG");
					b.setIcon(evac);
				}
				if(u instanceof GasControlUnit) {
					ImageIcon gas= new ImageIcon("gas.PNG");
					b.setIcon(gas);
				}
				if(u instanceof FireTruck) {
					ImageIcon fire= new ImageIcon("fire.PNG");
					b.setIcon(fire);
				}
				if(u instanceof DiseaseControlUnit) {
					ImageIcon disease= new ImageIcon("disease.PNG");
					b.setIcon(disease);
				}
				
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						flag=true;
						unit=u;
						dispunit(u);
						
					}
				});
				SimGUI.addavunitbutton(b);
			}
			else if(u.getState()!=UnitState.IDLE){
				JButton b= new JButton();
				if(u instanceof Ambulance) {
					ImageIcon ambulance= new ImageIcon("amb.PNG");
					b.setIcon(ambulance);
				}
				if(u instanceof Evacuator) {
					ImageIcon evac= new ImageIcon("evac.PNG");
					b.setIcon(evac);
				}
				if(u instanceof GasControlUnit) {
					ImageIcon gas= new ImageIcon("gas.PNG");
					b.setIcon(gas);
				}
				if(u instanceof FireTruck) {
					ImageIcon fire= new ImageIcon("fire.PNG");
					b.setIcon(fire);
				}
				if(u instanceof DiseaseControlUnit) {
					ImageIcon disease= new ImageIcon("disease.PNG");
					b.setIcon(disease);
				}
				
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						flag=true;
						unit=u;
						dispunit(u);
						
					}
				});
				SimGUI.addrespunitbutton(b);
			}
		}
	}

	public void dispbuilding(ResidentialBuilding b) {
		SimGUI.getInfo1().removeAllElements();
		
		String location="Location: "+b.getLocation().toString();
		String structinteg="Structural Integrity: "+b.getStructuralIntegrity();
		String firedmg="Fire Damage: "+b.getFireDamage();
		String gaslvl="Gas Level: "+b.getGasLevel();
		String foundationdmg="Foundation Damage: "+b.getFoundationDamage();
		String numocc="Number of Occupants: "+b.getOccupants().size();
		String distype="Disaster: "+b.getDisaster().getClass().getSimpleName();

		SimGUI.addrescuableinfo(location);
		SimGUI.addrescuableinfo(structinteg);
		SimGUI.addrescuableinfo(firedmg);
		SimGUI.addrescuableinfo(gaslvl);
		SimGUI.addrescuableinfo(foundationdmg);
		SimGUI.addrescuableinfo(numocc);
		SimGUI.addrescuableinfo(distype);
		if(!b.getOccupants().isEmpty()) {
			String occupants="Occupants:";
			SimGUI.addrescuableinfo(occupants);
			int count=1;
			for(Citizen c:b.getOccupants()) {
				String occupant_count="    Occupant "+count+":";
				SimGUI.addrescuableinfo(occupant_count);
				dispoccupant(c);
				SimGUI.addrescuableinfo(" ");
				count++;
			}
		}
	}
	
	public void dispoccupant(Citizen c) {
		String name="    Name: "+c.getName();
		String location="    Location: "+c.getLocation().toString();
		String age="    Age: "+c.getAge();
		String id="    National ID: "+c.getNationalID();
		String hp="    HP: "+c.getHp();
		String blood="    Bloodloss: "+c.getBloodLoss();
		String toxicity="    Toxicity: "+c.getToxicity();
		String state="    State: "+c.getState();
		
		SimGUI.addrescuableinfo(name);
		SimGUI.addrescuableinfo(location);
		SimGUI.addrescuableinfo(age);
		SimGUI.addrescuableinfo(id);
		SimGUI.addrescuableinfo(hp);
		SimGUI.addrescuableinfo(blood);
		SimGUI.addrescuableinfo(toxicity);
		SimGUI.addrescuableinfo(state);
	}
	
	public void dispcitizen(Citizen c) {
		SimGUI.getInfo1().removeAllElements();
		
		String name="Name: "+c.getName();
		String location="Location: "+c.getLocation().toString();
		String age="Age: "+c.getAge();
		String id="National ID: "+c.getNationalID();
		String hp="HP: "+c.getHp();
		String blood="Bloodloss: "+c.getBloodLoss();
		String toxicity="Toxicity: "+c.getToxicity();
		String state="State: "+c.getState();
		String disaster="Disaster: "+c.getDisaster().getClass().getSimpleName();
		
		SimGUI.addrescuableinfo(name);
		SimGUI.addrescuableinfo(location);
		SimGUI.addrescuableinfo(age);
		SimGUI.addrescuableinfo(id);
		SimGUI.addrescuableinfo(hp);
		SimGUI.addrescuableinfo(blood);
		SimGUI.addrescuableinfo(toxicity);
		SimGUI.addrescuableinfo(state);
		SimGUI.addrescuableinfo(disaster);
	}
	
	public void dispunit(Unit u) {
		SimGUI.getUnitinfo1().removeAllElements();
		
		String id="ID: "+u.getUnitID();
		SimGUI.addunitinfo(id);
		if(u instanceof Ambulance) {
			String type="Type: Ambulance";
			SimGUI.addunitinfo(type);
		}
		else if(u instanceof GasControlUnit) {
			String type="Type: Gas Control Unit";
			SimGUI.addunitinfo(type);
		}
		else if(u instanceof Evacuator) {
			String type="Type: Evacuator";
			SimGUI.addunitinfo(type);
		}
		else if(u instanceof FireTruck) {
			String type="Type: Fire Truck";
			SimGUI.addunitinfo(type);
		}
		else if(u instanceof DiseaseControlUnit) {
			String type="Type: Disease Control Unit";
			SimGUI.addunitinfo(type);
		}
		String location="Location: "+u.getLocation().toString();
		SimGUI.addunitinfo(location);
		String steps="Steps Per Cycle: "+u.getStepsPerCycle();
		SimGUI.addunitinfo(steps);
		if(u.getTarget()!=null) {
			String target=u.getTarget().getClass().getSimpleName()+ " at cell "+u.getTarget().getLocation().toString();
			SimGUI.addunitinfo(target);
		}
		String state="State: "+u.getState();
		SimGUI.addunitinfo(state);
		
		if(u instanceof Evacuator) {
			String numpass="Number of Passengers: "+((Evacuator)u).getPassengers().size();
			SimGUI.addunitinfo(numpass);
			
			if(((Evacuator) u).getPassengers().size()!=0) {
			String intropass="Passengers:";
			SimGUI.addunitinfo(intropass);
			}
			int count=1;
			for(int i=0;i<((Evacuator)u).getPassengers().size();i++) {
				String passcount="    Passenger "+count+":";
				SimGUI.addunitinfo(passcount);
				Citizen c=((Evacuator)u).getPassengers().get(i);
				disppassengers(c);
				SimGUI.addunitinfo(" ");
				count++;
			}
		}
	}
	public void disppassengers(Citizen c) {
		
		String name="Name: "+c.getName();
		SimGUI.addunitinfo(name);
		String location="Location: "+c.getLocation().toString();
		SimGUI.addunitinfo(location);
		String age="Age: "+c.getAge();
		SimGUI.addunitinfo(age);
		String id="National ID: "+c.getNationalID();
		SimGUI.addunitinfo(id);
		String hp="HP: "+c.getHp();
		SimGUI.addunitinfo(hp);
		String blood="Bloodloss: "+c.getBloodLoss();
		SimGUI.addunitinfo(blood);
		String toxicity="Toxicity: "+c.getToxicity();
		SimGUI.addunitinfo(toxicity);
		String state="State: "+c.getState();
		SimGUI.addunitinfo(state);
		if(c.getDisaster()!=null) {
			String disaster="Disaster: "+c.getDisaster().getClass().getSimpleName();
			SimGUI.addunitinfo(disaster);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		new CommandCenter();
	}

	

	@Override
	public void receiveSOSCall(Rescuable r) {
		
		if (r instanceof ResidentialBuilding) {
			
			if (!visibleBuildings.contains(r))
				visibleBuildings.add((ResidentialBuilding) r);
			
		} else {
			
			if (!visibleCitizens.contains(r))
				visibleCitizens.add((Citizen) r);
		}

	}

}
