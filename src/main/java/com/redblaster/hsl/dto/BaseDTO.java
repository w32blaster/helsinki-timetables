package com.redblaster.hsl.dto;

/**
 * Base DTO object
 * 
 * @author Ilja Hamalainen
 *
 */
public class BaseDTO {
	private long id;
	private String name;
	
	public BaseDTO(Long lId, String strName){
		this.id = lId;
		this.name = strName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseDTO [id=" + id + ", name=" + name + "]";
	}
	
}