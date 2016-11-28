package eu.cityopt.web;

import java.sql.Date;
import java.util.Map;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class PasswordForm {
	@Getter @Setter private String oldPassword;
}