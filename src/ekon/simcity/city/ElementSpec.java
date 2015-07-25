package ekon.simcity.city;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class ElementSpec {
  private ElementType type;
  private int width, height;

  ElementSpec(ElementType type, int width, int height) {
	this.type = type;
	this.width = width;
	this.height = height;
  }

  ElementType getType() {
	return type;
  }

  int getWidth() {
	return width;
  }

  int getHeight() {
	return height;
  }

  @Override
  public String toString() {
	return MoreObjects.toStringHelper(this)
		.add("type", type)
		.add("width", width)
		.add("height", height)
		.toString();
  }

  @Override
  public int hashCode() {
	return Objects.hash(type, width, height);
  }

  @Override
  public boolean equals(Object other) {
	if (!(other instanceof ElementSpec)) return false;
	ElementSpec o = (ElementSpec)other;
	return Objects.equals(getType(), o.getType())
		&& Objects.equals(getWidth(), o.getWidth())
		&& Objects.equals(getHeight(), o.getHeight());
  }
}
