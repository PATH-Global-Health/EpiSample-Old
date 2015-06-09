/*
 * Copyright© 2015 PATH
 *
 * Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License 
 * is distributed on an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express 
 * or implied. See the License for the specific language governing permissions and limitations under 
 * the License.
 *
 */

package org.path.episample.android.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.path.common.android.data.CensusModel;
import org.path.common.android.utilities.CensusUtil;
import org.path.common.android.utilities.ODKFileUtils;

import android.content.Context;

/*
 * @author belendia@gmail.com
 */

public class BackupRestoreUtils {
	public static class BackupResult {
		private boolean success;

		public boolean getSuccess() {
			return success;
		}

		public void setSuccess(boolean value) {
			success = value;
		}

		private String databaseFileName;

		public String getDatabaseFileName() {
			return databaseFileName;
		}

		public void setDatabaseFileName(String value) {
			databaseFileName = value;
		}
	}

	public static BackupResult simpleBackup(Context context, String appName) {
		BackupResult result = new BackupResult();
		String dbName = ODKFileUtils.getBackupFolder(appName)
				+ generateFileName(context);
		result.setDatabaseFileName(dbName);

		FileInputStream fromFile = null;
		FileOutputStream toFile = null;

		FileChannel from = null;
		FileChannel to = null;
		try {
			fromFile = new FileInputStream(
					ODKFileUtils.getCensusDbFullPath(appName));
			toFile = new FileOutputStream(dbName);
			from = fromFile.getChannel();
			to = toFile.getChannel();
			from.transferTo(0, from.size(), to);
			result.setSuccess(true);
		} catch (FileNotFoundException e) {
			result.setSuccess(false);
			e.printStackTrace();
		} catch (IOException e) {
			result.setSuccess(false);
			e.printStackTrace();
		} finally {
			if (fromFile != null) {
				try {
					fromFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (toFile != null) {
				try {
					toFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	private static String generateFileName(Context context) {
		String dbName = "";

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");
		String currentDate = sdf.format(calendar.getTime());

		CensusModel census = CensusUtil
				.getLastRecordHouseNumberPlusOne(context);
		dbName = census.getPlaceName() + "-" + currentDate + ".db";

		return dbName;
	}

	public static void backupCensus(Context context, String appName) {
		if (CensusUtil.getCount(context) > 0) {
			// CensusUtil.BackupResult result = CensusUtil.simpleBackup(context,
			// appName);
			BackupRestoreUtils.simpleBackup(context, appName);
		}
	}

	public static class BackupDatabase {
		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String value) {
			path = value;
		}

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String value) {
			name = value;
		}

		private String dateCreated;

		public String getDateCreated() {
			return dateCreated;
		}

		public void setDateCreated(String value) {
			dateCreated = value;
		}

		private Long longCateCreated;

		public Long getLongDateCreated() {
			return longCateCreated;
		}

		public void setLongDateCreated(Long value) {
			longCateCreated = value;
		}
	}

	public static List<BackupDatabase> getAllBackups(String appName) {
		List<BackupDatabase> result = new ArrayList<BackupDatabase>();
		File backupDir = new File(ODKFileUtils.getBackupFolder(appName));
		File[] files = backupDir.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".db")) {
				BackupDatabase dbFile = new BackupDatabase();
				dbFile.setPath(file.getPath());
				dbFile.setName(file.getName());

				Date lastModified = new Date(file.lastModified());
				SimpleDateFormat formatter = new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss");
				String formattedDateString = formatter.format(lastModified);

				dbFile.setDateCreated(formattedDateString);
				dbFile.setLongDateCreated(file.lastModified());
				result.add(dbFile);
			}
		}

		return result;
	}

	public static boolean dbFileExists(String dbFile) {
		File file = new java.io.File(dbFile);
		return file.exists();
	}

	public static boolean deleteDbFile(String dbFile) {
		File file = new java.io.File(dbFile);
		return file.delete();
	}

	/*
	 * public static boolean restore(Context context, String appName, String
	 * fromDbFile) {
	 * 
	 * boolean success = false; String toDbFile =
	 * ODKFileUtils.getCensusDbFullPath(appName);
	 * 
	 * if(deleteDbFile(toDbFile)) {
	 * 
	 * FileInputStream fromFile = null; FileOutputStream toFile = null;
	 * 
	 * FileChannel from = null; FileChannel to = null; try { fromFile = new
	 * FileInputStream(fromDbFile); toFile = new FileOutputStream(toDbFile);
	 * from = fromFile.getChannel(); to = toFile.getChannel();
	 * from.transferTo(0, from.size(), to); success = true; } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e)
	 * { e.printStackTrace(); } finally { if (fromFile != null) { try {
	 * fromFile.close(); } catch (IOException e) { e.printStackTrace(); } }
	 * 
	 * if (toFile != null) { try { toFile.close(); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * if (from != null) { try { from.close(); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * if (to != null) { try { to.close(); } catch (IOException e) {
	 * e.printStackTrace(); } } } }
	 * 
	 * return success; }
	 */

}
