package com.bruynhuis.galago.emitter;

import com.jme3.math.FastMath;

/**
 *
 * @author t0neg0d
 * Based on original code from Nathan Sweet
 */
public abstract class Interpolation {
	/** @param a blend value between 0 and 1. */
	abstract public float apply (float a);

	/** @param a blend value between 0 and 1. */
	public float apply (float start, float end, float a) {
		return start + (end - start) * apply(a);
	}

	static public final Interpolation linear = new Interpolation() {
		@Override
		public float apply (float a) {
				return a;
		}
	};

	static public final Interpolation fade = new Interpolation() {
		@Override
		public float apply (float a) {
				return FastMath.clamp(a * a * a * (a * (a * 6 - 15) + 10), 0, 1);
		}
	};

	static public final Pow pow2 = new Pow(2);
	static public final PowIn pow2In = new PowIn(2);
	static public final PowOut pow2Out = new PowOut(2);

	static public final Pow pow3 = new Pow(3);
	static public final PowIn pow3In = new PowIn(3);
	static public final PowOut pow3Out = new PowOut(3);

	static public final Pow pow4 = new Pow(4);
	static public final PowIn pow4In = new PowIn(4);
	static public final PowOut pow4Out = new PowOut(4);

	static public final Pow pow5 = new Pow(5);
	static public final PowIn pow5In = new PowIn(5);
	static public final PowOut pow5Out = new PowOut(5);

	static public final Interpolation sine = new Interpolation() {
		@Override
		public float apply (float a) {
				return (1 - FastMath.cos(a * FastMath.PI)) / 2;
		}
	};

	static public final Interpolation sineIn = new Interpolation() {
		@Override
		public float apply (float a) {
				return 1 - FastMath.cos(a * FastMath.PI / 2);
		}
	};

	static public final Interpolation sineOut = new Interpolation() {
		@Override
		public float apply (float a) {
				return FastMath.sin(a * FastMath.PI / 2);
		}
	};

	static public final Interpolation exp10 = new Exp(2, 10);
	static public final Interpolation exp10In = new ExpIn(2, 10);
	static public final Interpolation exp10Out = new ExpOut(2, 10);

	static public final Interpolation exp5 = new Exp(2, 5);
	static public final Interpolation exp5In = new ExpIn(2, 5);
	static public final Interpolation exp5Out = new ExpOut(2, 5);

	static public final Interpolation circle = new Interpolation() {
		@Override
		public float apply (float a) {
				if (a <= 0.5f) {
						a *= 2;
						return (1 - (float)Math.sqrt(1 - a * a)) / 2;
				}
				a--;
				a *= 2;
				return ((float)Math.sqrt(1 - a * a) + 1) / 2;
		}
	};

	static public final Interpolation circleIn = new Interpolation() {
		@Override
		public float apply (float a) {
				return 1 - (float)Math.sqrt(1 - a * a);
		}
	};

	static public final Interpolation circleOut = new Interpolation() {
		@Override
		public float apply (float a) {
				a--;
				return (float)Math.sqrt(1 - a * a);
		}
	};

	static public final Elastic elastic = new Elastic(2, 10);
	static public final Elastic elasticIn = new ElasticIn(2, 10);
	static public final Elastic elasticOut = new ElasticOut(2, 10);

	static public final Interpolation swing = new Swing(1.5f);
	static public final Interpolation swingIn = new SwingIn(2f);
	static public final Interpolation swingOut = new SwingOut(2f);

	static public final Interpolation bounce = new Bounce(4);
	static public final Interpolation bounceIn = new BounceIn(4);
	static public final Interpolation bounceOut = new BounceOut(4);

	//

	static public class Pow extends Interpolation {
		final int power;

		public Pow (int power) {
				this.power = power;
		}

		@Override
		public float apply (float a) {
				if (a <= 0.5f) return (float)Math.pow(a * 2, power) / 2;
				return (float)Math.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
		}
	}

	static public class PowIn extends Pow {
		public PowIn (int power) {
				super(power);
		}

		@Override
		public float apply (float a) {
				return (float)Math.pow(a, power);
		}
	}

	static public class PowOut extends Pow {
		public PowOut (int power) {
				super(power);
		}

		@Override
		public float apply (float a) {
				return (float)Math.pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
		}
	}

	//

	static public class Exp extends Interpolation {
		final float value, power, min, scale;

		public Exp (float value, float power) {
				this.value = value;
				this.power = power;
				min = (float)Math.pow(value, -power);
				scale = 1 / (1 - min);
		}

		@Override
		public float apply (float a) {
				if (a <= 0.5f) return ((float)Math.pow(value, power * (a * 2 - 1)) - min) * scale / 2;
				return (2 - ((float)Math.pow(value, -power * (a * 2 - 1)) - min) * scale) / 2;
		}
	};

	static public class ExpIn extends Exp {
		public ExpIn (float value, float power) {
				super(value, power);
		}

		@Override
		public float apply (float a) {
				return ((float)Math.pow(value, power * (a - 1)) - min) * scale;
		}
	}

	static public class ExpOut extends Exp {
		public ExpOut (float value, float power) {
				super(value, power);
		}

		@Override
		public float apply (float a) {
				return 1 - ((float)Math.pow(value, -power * a) - min) * scale;
		}
	}

	//

	static public class Elastic extends Interpolation {
		final float value, power;

		public Elastic (float value, float power) {
				this.value = value;
				this.power = power;
		}

		@Override
		public float apply (float a) {
				if (a <= 0.5f) {
						a *= 2;
						return (float)Math.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f / 2;
				}
				a = 1 - a;
				a *= 2;
				return 1 - (float)Math.pow(value, power * (a - 1)) * FastMath.sin((a) * 20) * 1.0955f / 2;
		}
	}

	static public class ElasticIn extends Elastic {
		public ElasticIn (float value, float power) {
				super(value, power);
		}

		@Override
		public float apply (float a) {
				return (float)Math.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f;
		}
	}

	static public class ElasticOut extends Elastic {
		public ElasticOut (float value, float power) {
				super(value, power);
		}

		@Override
		public float apply (float a) {
				a = 1 - a;
				return (1 - (float)Math.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f);
		}
	}

	static public class Bounce extends BounceOut {
		public Bounce (float[] widths, float[] heights) {
				super(widths, heights);
		}

		public Bounce (int bounces) {
				super(bounces);
		}

		private float out (float a) {
				float test = a + widths[0] / 2;
				if (test < widths[0]) return test / (widths[0] / 2) - 1;
				return super.apply(a);
		}

		@Override
		public float apply (float a) {
				if (a <= 0.5f) return (1 - out(1 - a * 2)) / 2;
				return out(a * 2 - 1) / 2 + 0.5f;
		}
	}

	static public class BounceOut extends Interpolation {
		final float[] widths, heights;

		public BounceOut (float[] widths, float[] heights) {
				if (widths.length != heights.length)
						throw new IllegalArgumentException("Must be the same number of widths and heights.");
				this.widths = widths;
				this.heights = heights;
		}

		public BounceOut (int bounces) {
				if (bounces < 2 || bounces > 5) throw new IllegalArgumentException("bounces cannot be < 2 or > 5: " + bounces);
				widths = new float[bounces];
				heights = new float[bounces];
				heights[0] = 1;
				switch (bounces) {
				case 2:
						widths[0] = 0.6f;
						widths[1] = 0.4f;
						heights[1] = 0.33f;
						break;
				case 3:
						widths[0] = 0.4f;
						widths[1] = 0.4f;
						widths[2] = 0.2f;
						heights[1] = 0.33f;
						heights[2] = 0.1f;
						break;
				case 4:
						widths[0] = 0.34f;
						widths[1] = 0.34f;
						widths[2] = 0.2f;
						widths[3] = 0.15f;
						heights[1] = 0.26f;
						heights[2] = 0.11f;
						heights[3] = 0.03f;
						break;
				case 5:
						widths[0] = 0.3f;
						widths[1] = 0.3f;
						widths[2] = 0.2f;
						widths[3] = 0.1f;
						widths[4] = 0.1f;
						heights[1] = 0.45f;
						heights[2] = 0.3f;
						heights[3] = 0.15f;
						heights[4] = 0.06f;
						break;
				}
				widths[0] *= 2;
		}

		@Override
		public float apply (float a) {
				a += widths[0] / 2;
				float width = 0, height = 0;
				for (int i = 0, n = widths.length; i < n; i++) {
						width = widths[i];
						if (a <= width) {
								height = heights[i];
								break;
						}
						a -= width;
				}
				a /= width;
				float z = 4 / width * height * a;
				return 1 - (z - z * a) * width;
		}
	}
	
	static public class BounceIn extends BounceOut {
		public BounceIn (float[] widths, float[] heights) {
				super(widths, heights);
		}

		public BounceIn (int bounces) {
				super(bounces);
		}

		@Override
		public float apply (float a) {
				return 1 - super.apply(1 - a);
		}
	}

	static public class Swing extends Interpolation {
		private final float scale;

		public Swing (float scale) {
				this.scale = scale * 2;
		}

		@Override
		public float apply (float a) {
				if (a <= 0.5f) {
						a *= 2;
						return a * a * ((scale + 1) * a - scale) / 2;
				}
				a--;
				a *= 2;
				return a * a * ((scale + 1) * a + scale) / 2 + 1;
		}
	}

	static public class SwingOut extends Interpolation {
		private final float scale;

		public SwingOut (float scale) {
				this.scale = scale;
		}

		@Override
		public float apply (float a) {
				a--;
				return a * a * ((scale + 1) * a + scale) + 1;
		}
	}

	static public class SwingIn extends Interpolation {
		private final float scale;

		public SwingIn (float scale) {
				this.scale = scale;
		}

		@Override
		public float apply (float a) {
				return a * a * ((scale + 1) * a - scale);
		}
	}
	public static String getInterpolationName(Interpolation interp) {
		String ret = "";
		if (interp == Interpolation.bounce) ret = "bounce";
		else if (interp == Interpolation.bounceIn) ret = "bounceIn";
		else if (interp == Interpolation.bounceOut) ret = "bounceOut";
		else if (interp == Interpolation.circle) ret = "circle";
		else if (interp == Interpolation.circleIn) ret = "circleIn";
		else if (interp == Interpolation.circleOut) ret = "circleOut";
		else if (interp == Interpolation.elastic) ret = "elastic";
		else if (interp == Interpolation.elasticIn) ret = "elasticIn";
		else if (interp == Interpolation.elasticOut) ret = "elasticOut";
		else if (interp == Interpolation.exp10) ret = "exp10";
		else if (interp == Interpolation.exp10In) ret = "exp10In";
		else if (interp == Interpolation.exp10Out) ret = "exp10Out";
		else if (interp == Interpolation.exp5) ret = "exp5";
		else if (interp == Interpolation.exp5In) ret = "exp5In";
		else if (interp == Interpolation.exp5Out) ret = "exp5Out";
		else if (interp == Interpolation.fade) ret = "fade";
		else if (interp == Interpolation.linear) ret = "linear";
		else if (interp == Interpolation.pow2) ret = "pow2";
		else if (interp == Interpolation.pow2In) ret = "pow2In";
		else if (interp == Interpolation.pow2Out) ret = "pow2Out";
		else if (interp == Interpolation.pow3) ret = "pow3";
		else if (interp == Interpolation.pow3In) ret = "pow3In";
		else if (interp == Interpolation.pow3Out) ret = "pow3Out";
		else if (interp == Interpolation.pow4) ret = "pow4";
		else if (interp == Interpolation.pow4In) ret = "pow4In";
		else if (interp == Interpolation.pow4Out) ret = "pow4Out";
		else if (interp == Interpolation.pow5) ret = "pow5";
		else if (interp == Interpolation.pow5In) ret = "pow5In";
		else if (interp == Interpolation.pow5Out) ret = "pow5Out";
		else if (interp == Interpolation.sine) ret = "sine";
		else if (interp == Interpolation.sineIn) ret = "sineIn";
		else if (interp == Interpolation.sineOut) ret = "sineOut";
		else if (interp == Interpolation.swing) ret = "swing";
		else if (interp == Interpolation.swingIn) ret = "swingIn";
		else if (interp == Interpolation.swingOut) ret = "swingOut";
		return ret;
	}
	public static Interpolation getInterpolationByName(String name) {
		Interpolation ret = null;
		if (name.equals("bounce")) ret = Interpolation.bounce;
		else if (name.equals("bounceIn")) ret = Interpolation.bounceIn;
		else if (name.equals("bounceOut")) ret = Interpolation.bounceOut;
		else if (name.equals("circle")) ret = Interpolation.circle;
		else if (name.equals("circleIn")) ret = Interpolation.circleIn;
		else if (name.equals("circleOut")) ret = Interpolation.circleOut;
		else if (name.equals("elastic")) ret = Interpolation.elastic;
		else if (name.equals("elasticIn")) ret = Interpolation.elasticIn;
		else if (name.equals("elasticOut")) ret = Interpolation.elasticOut;
		else if (name.equals("exp10")) ret = Interpolation.exp10;
		else if (name.equals("exp10In")) ret = Interpolation.exp10In;
		else if (name.equals("exp10Out")) ret = Interpolation.exp10Out;
		else if (name.equals("exp5")) ret = Interpolation.exp5;
		else if (name.equals("exp5In")) ret = Interpolation.exp5In;
		else if (name.equals("exp5Out")) ret = Interpolation.exp5Out;
		else if (name.equals("fade")) ret = Interpolation.fade;
		else if (name.equals("linear")) ret = Interpolation.linear;
		else if (name.equals("pow2")) ret = Interpolation.pow2;
		else if (name.equals("pow2In")) ret = Interpolation.pow2In;
		else if (name.equals("pow2Out")) ret = Interpolation.pow2Out;
		else if (name.equals("pow3")) ret = Interpolation.pow3;
		else if (name.equals("pow3In")) ret = Interpolation.pow3In;
		else if (name.equals("pow3Out")) ret = Interpolation.pow3Out;
		else if (name.equals("pow4")) ret = Interpolation.pow4;
		else if (name.equals("pow4In")) ret = Interpolation.pow4In;
		else if (name.equals("pow4Out")) ret = Interpolation.pow4Out;
		else if (name.equals("pow5")) ret = Interpolation.pow5;
		else if (name.equals("pow5In")) ret = Interpolation.pow5In;
		else if (name.equals("pow5Out")) ret = Interpolation.pow5Out;
		else if (name.equals("sine")) ret = Interpolation.sine;
		else if (name.equals("sineIn")) ret = Interpolation.sineIn;
		else if (name.equals("sineOut")) ret = Interpolation.sineOut;
		else if (name.equals("swing")) ret = Interpolation.swing;
		else if (name.equals("swingIn")) ret = Interpolation.swingIn;
		else if (name.equals("swingOut")) ret = Interpolation.swingOut;
		return ret;
	}
}
