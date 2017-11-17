package com.revature.dao;

import java.sql.Blob;
import java.sql.SQLException;

import com.revature.beans.Reimbursement;

/**
 * Functional interface for loading file blobs
 * @author Mitch Goshorn
 */
public interface ImageLoading {
	Blob getImage(Reimbursement reimbursement) throws SQLException;
}
