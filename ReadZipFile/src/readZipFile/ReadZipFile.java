package readZipFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
		readZipFile.readZip(fileName);
	}

	private void readZip(String fileName)
	{
		ZipFile zipFile;

		try (FileInputStream fis = new FileInputStream(fileName);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ZipInputStream zipInputStream = new ZipInputStream(bis))
		{
			zipFile = new ZipFile(fileName);

			ZipEntry zipEntry;

			while ((zipEntry = zipInputStream.getNextEntry()) != null)
			{
				if (zipEntry.getName().contains(".zip"))
				{
					nestedZip(zipEntry, zipInputStream);
				} else
				{
					readDicomFile(zipFile, zipEntry);
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void nestedZip(ZipEntry zipEntry, ZipInputStream zipInputStream)
	{
		byte[] buffer = new byte[1024];
		System.out.println(zipEntry.getName());
		File newFile = new File("D:\\Dicom file\\New folder\\" + zipEntry.getName());
		new File(newFile.getParent()).mkdirs();
		FileOutputStream fileOutputStream;
		try
		{
			fileOutputStream = new FileOutputStream(newFile);
			int length;
			while ((length = zipInputStream.read(buffer)) > 0)
			{
				fileOutputStream.write(buffer, 0, length);
			}
			fileOutputStream.close();
			readZip(newFile.getAbsolutePath());
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void readDicomFile(ZipFile zipFile, ZipEntry zipEntry)
	{
		DicomInputStream dicomInputStream;
		try
		{
			dicomInputStream = new DicomInputStream(zipFile.getInputStream(zipEntry));

			AttributeList attributeList = new AttributeList();

			attributeList.read(dicomInputStream);

			System.out.println(zipEntry.getName() + " : "
					+ Attribute.getDelimitedStringValuesOrEmptyString(attributeList, TagFromName.SOPClassUID));
			dicomInputStream.close();

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
