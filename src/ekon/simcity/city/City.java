package ekon.simcity.city;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class City {
  // TODO: add more required services.
  private static final List<ElementType> REQUIRED_SERVICE_TYPES =
	  Arrays.asList(ElementType.FIRE_STATION, ElementType.POLICE_STATION);

  private final Position cityLimits;
  private final Multimap<ElementType, GridElement> gridElementsByType;
  private final Multimap<Position, GridElement> gridElementsByPosition;
  private final ConstraintVerifier verifier;

  public City(Position cityLimits) {
	this.cityLimits = cityLimits;
	this.gridElementsByType = ArrayListMultimap.create();
	this.gridElementsByPosition = ArrayListMultimap.create();
	this.verifier = new ConstraintVerifier();
  }

  public void add(GridElement element) {
	Position position = element.getPosition();
	if (gridElementsByPosition.containsKey(position)) {
	  throw new IllegalStateException(
		"\nThere is an existing element at that position: " + gridElementsByPosition.get(position));
	}
	gridElementsByType.put(element.getSpec().getType(), element);
	gridElementsByPosition.put(element.getPosition(), element);
  }

  public void remove(GridElement element) {
	Position position = element.getPosition();
	if (!gridElementsByPosition.containsEntry(position, element)) {
	  StringBuilder error = new StringBuilder("Trying to remove element that's not in the city: ").append(element);
	  if (gridElementsByType.containsKey(position)) {
		error.append("\nThere is an existing element at that position: ").append(gridElementsByPosition.get(position));
	  }
	  throw new IllegalStateException(error.toString());
	}
	gridElementsByType.remove(element.getSpec().getType(), element);
	gridElementsByPosition.remove(position, element);
  }

  // Element must be placed so that it satisfies constraints.
  // If doesn't then address the constraints not satisfied.
  // Returns true if element was placed successfully.
  public boolean tryToPlaceElement(ElementSpec spec) {
	// TODO: consider also keeping a list of taken positions (would need to
	// update add & remove calls to add to this list.
	Set<Position> takenPositions = gridElementsByPosition.keySet();
	
	// TODO: !!!!!this will limit what we can do - can't place a house before the req services are
	// placed. This means required services will always be at the edges and won't have their
	// coverage area utilization maximized.
	// A better approach might be to iterate through the required services and create a new
	// city for each area that they're placed in. Then iteratively add houses and roads.
	
	// Iterate through the grid trying to place the element.
	// Should we look for road? Or just place and then add if it's not there?
	for (int x = 0; x < cityLimits.getX(); x++) {
	  for (int y = 0; y < cityLimits.getY(); y++) {
		// TODO:
	  }
	}
	return true;
  }

  // Returns true if all the requirements for the city are satisfied:
  // - all elements are touching a road on at least 1 edge
  // - all houses are covered by required services (fire, water, etc.)
  public boolean areRequirementsSatisfied() {
	return verifier.verify();
  }

  @Override
  public String toString() {
	// Order grid elements by coordinates and print it out in a user-friendly
	// grid.

	// First find edges of grid to create 2D array modeling the grid.
	int maxX = 0, maxY = 0;
	for (GridElement element : gridElementsByType.values()) {
	  int x = element.getPosition().getX();
	  int y = element.getPosition().getY();
	  if (x > maxX) {
		maxX = x;
	  }
	  if (y > maxY) {
		maxY = y;
	  }
	}

	// create the grid and fill in the first letter of the type of element
	// in the coordinates it occupies.
	String[][] grid = new String[maxX + 1][maxY + 1];
	for (GridElement element : gridElementsByType.values()) {
	  grid[element.getPosition().getX()][element.getPosition().getY()] =
		  element.getSpec().getType().name().substring(0, 1);
	}

	StringBuilder str = new StringBuilder("Grid\n");
	for (int y = 0; y <= maxY; y++) {
	  str.append("-");
	}
	str.append("--\n"); // 2 extra dashes for the edge columns
	for (int x = 0; x <= maxX; x++) {
	  str.append("|");
	  for (int y = 0; y <= maxY; y++) {
		if ((grid[x][y] == null) || (grid[x][y] == "")) {
		  grid[x][y] = "-";
		}
		str.append(grid[x][y]);
	  }
	  str.append("|\n");
	}
	for (int y = 0; y <= maxY; y++) {
	  str.append("-");
	}
	str.append("--\n"); // 2 extra dashes for the edge columns

	// Print details for all services.
	str.append("GridElements [").append(gridElementsByType.values());

	return str.toString();
  }
  
 
  // Verifies constraints on elements in city.
  private class ConstraintVerifier {
	boolean verify() {
	  boolean requirementsSatisfied = true;
	  for (GridElement element : gridElementsByType.values()) {
		for (ElementConstraint constraint : element.getSpec().getType().getConstraints()) {
		  switch (constraint) {
			case NEAR_ROAD:
			  boolean isConnectedToRoad = isConnectedToRoad(element);
			  System.out.println("element " + element + " isConnectedToRoad? " + isConnectedToRoad);
			  requirementsSatisfied &= isConnectedToRoad;
			  break;
			case DOES_NOT_OVERLAP:
			  Position position = element.getPosition();
			  boolean overlaps = gridElementsByPosition.containsKey(position);
			  System.out.println("Element " + element + " overlaps? " + overlaps);
			  break;
			case COVERED_BY_REQUIRED_SERVICE:
			  boolean isCoveredByServices = isHouseCoveredByServices(element);
			  System.out.println("element " + element + " isCoveredByServices? " + isCoveredByServices);
			  requirementsSatisfied &= isCoveredByServices;
			  break;
			default:
			  throw new IllegalArgumentException("Unsupported ElementConstraint " + constraint);
		  }
		}
		// TODO add other requirements (e.g. power, water, sewer - these aren't
		// necessary for finding a good land configuration though)
	  }
	  return requirementsSatisfied;
	}

	// True if the given element is connected to a road.
	private boolean isConnectedToRoad(GridElement element) {
	  // TODO: I think direction it faces matters in some cases (e.g 2x1 park),
	  // so
	  // the width/height cannot be swapped.

	  // TODO: Looks like for the solar energy plant, not all of it has to be on
	  // a road apparently!

	  // Look at each edge to see if there is a road on the other side of that
	  // edge.
	  Position position = element.getPosition();
	  int x = position.getX();
	  int y = position.getY();
	  ElementSpec spec = element.getSpec();
	  int width = spec.getWidth();
	  int height = spec.getHeight();
	  int maxX = x + height - 1;
	  int maxY = y + width - 1;

	  // Top edge - go along x-1, starting at y and going to maxY.
	  boolean edgeHasRoad = true; // this will be true only if an edge is of
								  // length 0, or if the edge is covered by
								  // road.
	  for (int i = y; i <= maxY; i++) {
		Position coord = new Position(x - 1, i);
		edgeHasRoad &= isRoad(coord);
	  }
	  if (edgeHasRoad) return true;

	  // Bottom edge - go along maxX+1, starting at y and going to maxY.
	  edgeHasRoad = true; // reset
	  for (int i = y; i <= maxY; i++) {
		Position coord = new Position(maxX + 1, i);
		edgeHasRoad &= isRoad(coord);
	  }
	  if (edgeHasRoad) return true;

	  // Left edge - go along y-1, starting at x and going to maxX.
	  edgeHasRoad = true; // reset
	  for (int i = x; i <= maxX; i++) {
		Position coord = new Position(i, y - 1);
		edgeHasRoad &= isRoad(coord);
	  }
	  if (edgeHasRoad) return true;

	  // Right edge - go along maxY+1, starting at x and going to maxX.
	  edgeHasRoad = true; // reset
	  for (int i = x; i <= maxX; i++) {
		Position coord = new Position(i, maxY + 1);
		edgeHasRoad &= isRoad(coord);
	  }
	  return edgeHasRoad;
	}

	// Returns true if there is a road at the given position.
	private boolean isRoad(Position position) {
	  System.out.println("Looking for road at " + position);
	  // If there are no roads, should false because this is only called to
	  // check if
	  // there's a road next to an item and if there's no road, it's wrong.
	  boolean isRoad = false;
	  for (GridElement road : gridElementsByType.get(ElementType.ROAD)) {
		isRoad |= road.getPosition().equals(position);
	  }
	  if (isRoad) System.out.println("Found road at " + position);
	  return isRoad;
	}

	// True if a house at a given coordinate is covered by all required
	// services.
	private boolean isHouseCoveredByServices(GridElement house) {
	  if (!(house.getSpec().getType() == ElementType.HOUSE)) { throw new IllegalArgumentException(
		  "Expected HOUSE element but got " + house); }

	  // This will only return true if there are no required services or
	  // if all houses are covered by required services.
	  boolean isCovered = true;
	  // Find existing services in city of this type and check if their coverage
	  // area covers item.
	  for (ElementType serviceType : REQUIRED_SERVICE_TYPES) {
		// House only needs to be covered by one of each service type.
		boolean isCoveredByServiceType = false;
		for (GridElement gridElement : gridElementsByType.get(serviceType)) {
		  isCoveredByServiceType |= isElementCoveredByElement(house, gridElement);
		}
		isCovered &= isCoveredByServiceType;
	  }
	  return isCovered;
	}

	// Returns true is an element is covered by a given element.
	private boolean isElementCoveredByElement(GridElement coverableElement, GridElement coveringElement) {
	  if (!(coveringElement.getSpec() instanceof ServiceSpec)) {
		new IllegalArgumentException("element doesn't implement HasCoverageArea " + coveringElement);
	  }

	  // Verify if any part of the house overlaps with the given service.
	  // Do this by gathering that set of Positions for the house and looking
	  // for an intersection with the set of Positions of the service.
	  int coverableX = coverableElement.getPosition().getX();
	  int coverableY = coverableElement.getPosition().getY();
	  ElementSpec coverableSpec = coverableElement.getSpec();
	  Set<Position> coverablePositions = new HashSet<>();
	  for (int x = coverableX; x < coverableX + coverableSpec.getHeight(); x++) {
		for (int y = coverableY; y < coverableY + coverableSpec.getWidth(); y++) {
		  coverablePositions.add(new Position(x, y));
		}
	  }

	  // Note: coverage area could actually be outside of city limits. That's OK, but need to
	  // account for it in the calculations so that we don't go outside of bounds.
	  ServiceSpec coveringSpec = (ServiceSpec) coveringElement.getSpec();
	  Position startOfCoverage = getStartOfCoverage(coveringElement);
	  int coveringX = startOfCoverage.getX();
	  int coveringY = startOfCoverage.getY();
	  // Note: coverage could be outside of city limits, which is ok, but we want to account for that to not get IndexOutOfBounds.
	  int coveringMaxX = Math.max(cityLimits.getX(), coveringX + coveringSpec.getCoverageHeight() - 1);
	  int coveringMaxY = Math.max(cityLimits.getY(), coveringY + coveringSpec.getCoverageWidth() - 1);
	  Set<Position> coveringPositions = new HashSet<>();
	  for (int x = coveringX; x <= coveringMaxX; x++) {
		for (int y = coveringY; y < coveringMaxY; y++) {
		  coveringPositions.add(new Position(x, y));
		}
	  }

	  boolean isCovered = !Sets.intersection(coverablePositions, coveringPositions).isEmpty();
	  System.out
		  .println("Checking if element " + coverableElement + " is covered by " + coveringElement + " =" + isCovered);
	  return isCovered;
	}

	// Returns the Position of the start of coverage for a given element.
	private Position getStartOfCoverage(GridElement element) {
	  if (!(element.getSpec() instanceof ServiceSpec)) {
		new IllegalArgumentException("element doesn't implement HasCoverageArea " + element);
	  }

	  ServiceSpec spec = (ServiceSpec) element.getSpec();
	  Position position = element.getPosition();
	  // Note: coverage could be outside of city limits, which is ok, but we want to account for that to not get IndexOutOfBounds.
	  int x = Math.min(0, position.getX() - ((spec.getCoverageWidth() - spec.getWidth()) / 2));
	  int y = Math.min(0, position.getY() - ((spec.getCoverageHeight() - spec.getHeight()) / 2));
	  return new Position(x, y);
	}
  }
  
  private class Scorer {
	private int score;

	int getScore() {
	  score = 0;
	  return score;
	}
	
	private void scoreOnCoverage() {
	  // Want all service coverage areas to be utilized.
	  // Lower score if service's coverage area is outside of city limits.
	  
	  // Lower score if service's coverage area is covering roads.
	}
  }
}
