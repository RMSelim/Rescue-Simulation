package model.units;

import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import simulation.Address;
import simulation.Rescuable;

public class DiseaseControlUnit extends MedicalUnit {

	public DiseaseControlUnit(String unitID, Address location,
			int stepsPerCycle, WorldListener worldListener) {
		super(unitID, location, stepsPerCycle, worldListener);
	}

	@Override
	public void treat() {
		getTarget().getDisaster().setActive(false);
		Citizen target = (Citizen) getTarget();
		if (target.getHp() == 0) {
			jobsDone();
			return;
		} else if (target.getToxicity() > 0) {
			target.setToxicity(target.getToxicity() - getTreatmentAmount());
			if (target.getToxicity() == 0)
				target.setState(CitizenState.RESCUED);
		}

		else if (target.getToxicity() == 0)
			heal();

	}

	public void respond(Rescuable r) throws CannotTreatException, IncompatibleTargetException {
		if(r instanceof ResidentialBuilding){
			throw(new IncompatibleTargetException(this,r,"Unit is incompatible with this target"));
		}
		
		else if(canTreat(r)==false){
			throw(new CannotTreatException(this,r,"Citizen can not be treated"));
			
		}
		else if(r instanceof Citizen&&!(((Citizen) r).getDisaster() instanceof Infection)){
			throw(new CannotTreatException(this,r,"Citizen can not be treated"));	
		}
			
		else {
			if (getTarget() != null && ((Citizen) getTarget()).getToxicity() > 0
		
				&& getState() == UnitState.TREATING)
			reactivateDisaster();
		finishRespond(r);}
		}
		}
	

