package assignment5;

/**
 * 
 * @author rkandur
 *
 */
public class LamportClock {
	
	long timer_;
	private static int CLOCK_INCREMENTAL_UNIT = 1;
	
	public LamportClock() {
		timer_ = 0;
	}
	
	public long updateAndGetClockValue() {
		timer_ = timer_+ CLOCK_INCREMENTAL_UNIT;
		return timer_;
	}
	
	public long updateAndGetClockValue(long time) {
		timer_ = timer_ > time ? timer_+CLOCK_INCREMENTAL_UNIT : time+CLOCK_INCREMENTAL_UNIT;
		return timer_;
	}
	
	public long getClockValue() {
		return timer_;
	}

}
