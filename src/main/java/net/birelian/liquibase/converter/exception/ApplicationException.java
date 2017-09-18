/**
 * Copyright 2016 Guillermo Bauzá (birelian) - birelianATgmailDOTcom
 *
 *
 * This file is part of Liquibase File Converter Maven Plugin.
 *
 * Liquibase File Converter Maven Plugin is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Liquibase File Converter Maven Plugin is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with Liquibase File Converter Maven Plugin. If
 * not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.birelian.liquibase.converter.exception;

/**
 * Generic application exception
 */
public class ApplicationException extends Exception {

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApplicationException(String message) {
    super(message);
  }
}
