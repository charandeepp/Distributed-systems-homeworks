package assignment5;

/**
 * class which defines a Lamport Clock used by StateMachineModel
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
	
	// just increments/updates the current clock value and returns the latest clock value 
	public long updateAndGetClockValue() {
		timer_ = timer_+ CLOCK_INCREMENTAL_UNIT;
		return timer_;
	}
	
	// checks if the time stamp given as argument is > current clock value.
	// if yes, updates the clock value to the one passed in as an argument, then increments the updated clock value
	// if not, just increments the current clock value
	public long updateAndGetClockValue(long time) {
		timer_ = timer_ > time ? timer_+CLOCK_INCREMENTAL_UNIT : time+CLOCK_INCREMENTAL_UNIT;
		return timer_;
	}
	
	public long getClockValue() {
		return timer_;
	}

}
