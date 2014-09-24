package me.axiometry.tanks.world;

public abstract class AbstractWorld implements World {

	/**
	 * Check collision between two rotated rectangles
	 */
	protected final boolean checkCollision(Rectangle bounding1, double theta1,
			Rectangle bounding2, double theta2) {
		// double distance = Math.sqrt(Math.pow(bounding2.x - bounding1.x, 2)
		// + Math.pow(bounding2.y - bounding1.y, 2));
		// if("".equals(""))
		// return distance < Math.min(bounding1.width, bounding1.height) / 2D
		// + Math.min(bounding2.width, bounding2.height) / 2D;
		bounding1 = bounding1.clone();
		bounding2 = bounding2.clone();
		bounding2.x -= bounding1.x;
		bounding2.y -= bounding1.y;
		bounding1.x = 0;
		bounding1.y = 0;
		theta1 = Math.toRadians(theta1);
		theta2 = Math.toRadians(theta2);
		double dist = Math.sqrt(Math.pow(bounding2.x, 2)
				+ Math.pow(bounding2.y, 2));
		Rectangle alignedOtherBounding = new Rectangle(
				(int) (dist * Math.cos(theta1)),
				(int) (dist * Math.sin(theta1)), bounding2.width,
				bounding2.height);
		return checkAlignedCollision(bounding1, alignedOtherBounding, theta2
				- theta1);
	}

	private boolean checkAlignedCollision(Rectangle bounding1,
			Rectangle bounding2, double theta) {
		double r1 = Math.sqrt(Math.pow(bounding1.width / 2D, 2)
				+ Math.pow(bounding1.height / 2D, 2));
		double r2 = Math.sqrt(Math.pow(bounding2.width / 2D, 2)
				+ Math.pow(bounding2.height / 2D, 2));
		double cornerAngle1 = Math.atan2(bounding1.height, bounding1.width);
		double cornerAngle2 = theta
				+ Math.atan2(bounding2.height, bounding2.width);
		double x1A = x(r1, cornerAngle1, 0), x1B = x(r1, cornerAngle1, 1), x1C = x(
				r1, cornerAngle1, 2), x1D = x(r1, cornerAngle1, 3);
		double x2A = x(r2, cornerAngle2, 0), x2B = x(r2, cornerAngle2, 1), x2C = x(
				r2, cornerAngle2, 2), x2D = x(r2, cornerAngle2, 3);
		double y1A = y(r1, cornerAngle1, 0), y1B = y(r1, cornerAngle1, 1), y1C = y(
				r1, cornerAngle1, 2), y1D = y(r1, cornerAngle1, 3);
		double y2A = y(r2, cornerAngle2, 0), y2B = y(r2, cornerAngle2, 1), y2C = y(
				r2, cornerAngle2, 2), y2D = y(r2, cornerAngle2, 3);
		double x1Min = min(x1A, x1B, x1C, x1D), x1Max = max(x1A, x1B, x1C, x1D);
		double y1Min = min(y1A, y1B, y1C, y1D), y1Max = max(y1A, y1B, y1C, y1D);
		double x2Min = min(x2A, x2B, x2C, x2D), x2Max = max(x2A, x2B, x2C, x2D);
		double y2Min = min(y2A, y2B, y2C, y2D), y2Max = max(y2A, y2B, y2C, y2D);
		if(((x1Min < x2Min && x1Max > x2Max)
				|| (x2Min < x1Min && x2Max > x1Max)
				|| (x1Min < x2Min && x1Max < x2Max && x1Max > x2Min) || (x2Min < x1Min
				&& x2Max < x1Max && x2Max > x1Min))
				&& ((y1Min < y2Min && y1Max < y2Max && y1Max > y2Min)
						|| (y2Min < y1Min && y2Max < y1Max && y2Max > y1Min)
						|| (y1Min < y2Min && y1Max > y2Max) || (y2Min < y1Min && y2Max > y1Max)))
			return true;
		return false;
	}

	private double x(double radius, double theta, int corner) {
		return radius * Math.cos(corner * (Math.PI / 2D) + theta);
	}

	private double y(double radius, double theta, int corner) {
		return radius * Math.sin(corner * 90 + theta);
	}

	private double min(double... values) {
		if(values.length == 0)
			return Double.NaN;
		double min = values[0];
		for(int i = 1; i < values.length; i++)
			min = Math.min(min, values[i]);
		return min;
	}

	private double max(double... values) {
		if(values.length == 0)
			return Double.NaN;
		double min = values[0];
		for(int i = 1; i < values.length; i++)
			min = Math.max(min, values[i]);
		return min;
	}

	protected class Rectangle {
		public double x, y, width, height;

		public Rectangle(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public Rectangle clone() {
			return new Rectangle(x, y, width, height);
		}
	}
}
