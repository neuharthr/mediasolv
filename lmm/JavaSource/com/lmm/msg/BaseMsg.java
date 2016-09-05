package com.lmm.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseMsg implements Serializable {
	static final long serialVersionUID = 100002L;

	private Date msgDate = new Date();	//can be NULL
	private String name = "";
	private Map header = new HashMap();
	private String uuid = null;


	public static Comparator MsgNameComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			if( o1 instanceof BaseMsg
				&& o2 instanceof BaseMsg ) {
					BaseMsg msg1 = (BaseMsg)o1;
					BaseMsg msg2 = (BaseMsg)o2;
					return msg1.getName().compareTo( msg2.getName() );
			}
			return 0;
		}
	};

	public static Comparator MsgUUIDComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			if( o1 instanceof BaseMsg
				&& o2 instanceof BaseMsg ) {
					BaseMsg msg1 = (BaseMsg)o1;
					BaseMsg msg2 = (BaseMsg)o2;
					return msg1.getUuid().compareTo( msg2.getUuid() );
			}
			return 0;
		}
	};

	public BaseMsg() {
		super();
	}

	public int hashCode() {
		return getName().hashCode();
	}

	public boolean equals(Object val)  {
		if( val instanceof BaseMsg ) {
			return getName().equals( ((BaseMsg)val).getName() );
		}
		else
			return super.equals(val);
	}

	public void addHeader(String key, Object value) {
		header.put(key, value);
	}

	public Object getHeader(String key) {
		return header.get(key);
	}

    /**
     * @return
     */
    public Date getMsgDate() {
        return msgDate;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param date
     */
    public void setMsgDate(Date date) {
        msgDate = date;
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

	private void readObject( ObjectInputStream oi  ) throws IOException, ClassNotFoundException {
		oi.defaultReadObject();
	}

	private void writeObject( ObjectOutputStream oo  ) throws IOException {
		oo.defaultWriteObject();
	}

    /**
     * @return
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param string
     */
    public void setUuid(String string) {
        uuid = string;
    }

}
