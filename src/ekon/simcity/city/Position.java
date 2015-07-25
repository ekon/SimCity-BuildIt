package ekon.simcity.city;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class Position {
  private int x, y;

  public Position(int x, int y) {
	this.x = x;
	this.y = y;
  }

  int getX() {
	return x;
  }

  int getY() {
	return y;
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("x", x)
		.add("y", y)
		.toString();
  }
  
  @Override
  public int hashCode() {
	return Objects.hash(x, y);
  }
  
  @Override
  public boolean equals(Object other) {
	if (!(other instanceof Position)) return false;
	Position o = (Position)other;
	return Objects.equals(getX(), o.getX())
		&& Objects.equals(getY(), o.getY());
  }
}
