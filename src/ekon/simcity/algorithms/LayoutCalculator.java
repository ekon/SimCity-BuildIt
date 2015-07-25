package ekon.simcity.algorithms;

import java.util.Arrays;
import java.util.List;

import ekon.simcity.city.City;
import ekon.simcity.city.ElementFactory;
import ekon.simcity.city.ElementSpec;
import ekon.simcity.city.ElementType;
import ekon.simcity.city.Position;

/**
 * Calculates the best city layout.
 */
class LayoutCalculator {
  // Assuming 2 roads on each side and 4ish buildings.
  private static final Position BASIC_CITY_LIMITS = new Position(10, 10);

  private static void addElements(City city) {
	city.add(ElementFactory.getElement(ElementType.ROAD, new Position(0,0)));
	city.add(ElementFactory.getElement(ElementType.ROAD, new Position(0,1)));
	city.add(ElementFactory.getElement(ElementType.ROAD, new Position(0,2)));
	city.add(ElementFactory.getElement(ElementType.ROAD, new Position(0,3)));
	city.add(ElementFactory.getElement(ElementType.HOUSE, new Position(1, 0)));
	city.add(ElementFactory.getElement(ElementType.FIRE_STATION, new Position(1, 2)));
	city.add(ElementFactory.getElement(ElementType.POLICE_STATION, new Position(1, 3)));

	// overlapping element
//	city.add(ElementFactory.getElement(ElementType.ROAD, new Position(1,0)));
	
	// unattached road.
//	city.add(ElementFactory.getElement(ElementType.ROAD, new Position(3,1)));
  }

  /**
   * Given a set of required services, and constraint on grid area, come up with possible layouts.
   * New houses and roads can be added.
   */
  private void calculateLayoutWithConstraints(City city) {
	// Want to have a small one of each of the required services.
	List<ElementSpec> requiredElements = Arrays.asList(
		ElementFactory.getServiceSpec(ElementType.FIRE_STATION),
		ElementFactory.getServiceSpec(ElementType.POLICE_STATION));

	// Start out with a road at (0,0)
	city.add(ElementFactory.getElement(ElementType.ROAD, new Position(0,0)));
	
  }

  public static void main(String[] args) {
	City city = new City(BASIC_CITY_LIMITS);
	addElements(city);

	// Start with a road opening. First add some road, then let the algorithm
	// run.
	// algorithm, add something, see if all requirements are satisfied.
	// if requirements are satisfied, then save the configuration to memory.
	// when to stop adding? add a constraint for finishing.

	// iterate through saved configurations and find highest values (# of
	// houses, # of people), return the top configurations.

	System.out.println(city);
	System.out.println("req satisfied: " + city.areRequirementsSatisfied());
  }
}
