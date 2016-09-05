/*
 * DemoBean.java
 */

package swingbeanformbuilder.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple bean used in the SBFB demo.
 * 
 * @author Simon OUALID
 */
public class DemoBean {
    
    private long id = -1;
    private String firstname = "";
    private String lastname = "";
    private String sexe = "M";
    private String contrat = "CDI";
    private String comments = "";
    private String bankingComments = "";
    private int age = 0;
    private Date birthday = null;
    private String IBAN = "1 81 09 19 08 77";
    private BigDecimal rate = new BigDecimal(4.75);
    private transient List friends = new ArrayList();
    private boolean client = false;
    private boolean prospect = false;
    private String drink = "resources/cofee.jpg";
    private DemoBean dad = null;
    private boolean active = true;
    
    /** Creates a new instance of DemoBean */
    public DemoBean() {
    }

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getBankingComments() {
		return bankingComments;
	}

	public void setBankingComments(String bankingComments) {
		this.bankingComments = bankingComments;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public boolean isClient() {
		return client;
	}

	public void setClient(boolean client) {
		this.client = client;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getContrat() {
		return contrat;
	}

	public void setContrat(String contrat) {
		this.contrat = contrat;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public List getFriends() {
		return friends;
	}

	public void setFriends(List friends) {
		this.friends = friends;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public boolean isProspect() {
		return prospect;
	}

	public void setProspect(boolean prospect) {
		this.prospect = prospect;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public String getIBAN() {
		return IBAN;
	}

	public long getId() {
		return id;
	}


    public String toString() {
        return getFirstname() + " " + getLastname();
    }
    
    public boolean equals(Object obj) {
        return false;
    }

    public int hashCode() {
        return getLastname().hashCode() + getFirstname().hashCode() + new Long(id).hashCode();
    }

	public String getDrink() {
		return drink;
	}

	public DemoBean getDad() {
		return dad;
	}

	public void setDad(DemoBean dad) {
		this.dad = dad;
	}

	/**
	 * @return Returns the active.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active The active to set.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
}
