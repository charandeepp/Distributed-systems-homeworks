package assignment5;

import java.io.Serializable;

/**
 * Created by Charandeep on 3/31/15.
 */
public class PeerMsgType implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int RequestMsg = 1;
    public static final int AckMsg = 2;

    public int peer_msg_type;

    PeerMsgType(int req)
    {
        peer_msg_type = req;
    }

}
