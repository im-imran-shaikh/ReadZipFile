package readZipFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.TagFromName;

public class ReadZipFile
{
	public static void main(String args[])
	{
		ReadZipFile readZipFile = new ReadZipFile();

		String fileName = "D:\\Dicom file\\New folder\\zipping.zip";
		readZipFile.readWithZipInputStream(fileName);
	}

	private void readWithZipInputStream(String fileName)
	{
		ZipFile zipFile;
		byte[] buffer = new byte[1024];
		try
		{
			zipFile = new ZipFile(fileName);

			try (FileInputStream fis = new FileInputStream(fileName);
					BufferedInputStream bis = new BufferedInputStream(fis);
					ZipInputStream zipInputStream = new ZipInputStream(bis))
			{

				ZipEntry zipEntry;

				while ((zipEntry = zipInputStream.getNextEntry()) != null)
				{
					if (zipEntry.getName().contains(".zip"))
					{
						System.out.println(zipEntry.getName());
						File newFile = new File("D:\\Dicom file\\New folder\\" + zipEntry.getName());
						new File(newFile.getParent()).mkdirs();
						FileOutputStream fileOutputStream = new FileOutputStream(newFile);
						int len;
						while ((len = zipInputStream.read(buffer)) > 0)
						{
							fileOutputStream.write(buffer, 0, len);
						}
						fileOutputStream.close();
						readWithZipInputStream(newFile.getAbsolutePath());

					} else
					{

						DicomInputStream dicomInputStream = new DicomInputStream(zipFile.getInputStream(zipEntry));
						AttributeList attributeList = new AttributeList();

						try
						{
							attributeList.read(dicomInputStream);

							System.out.println(zipEntry.getName() + " : " + Attribute
									.getDelimitedStringValuesOrEmptyString(attributeList, TagFromName.SOPClassUID));

						} catch (Exception ex)
						{
							ex.printStackTrace();
						} finally
						{
							dicomInputStream.close();
						}
					}
				}

			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
