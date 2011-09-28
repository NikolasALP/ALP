//Copyright 2011 Lohika .  This file is part of ALP.
//
//    ALP is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    ALP is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with ALP.  If not, see <http://www.gnu.org/licenses/>.
package alp.lohika.com.utils.object.reader;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import alp.lohika.com.utils.object.reader.ExcelReader;
import alp.lohika.com.utils.object.reader.model.User;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ExcelReaderTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ExcelReaderTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ExcelReaderTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testExcelReaderAllObjects() throws Exception
    {
		ExcelReader reader = new ExcelReader("src/test/resources/login.xls");
		@SuppressWarnings("unchecked")
		List<User> users = (List<User>) reader.readAllObjects(User.class);
		for (User user: users)
			System.out.println("Name:"+user.getName()+" Password:"+user.getPassword());

		assertTrue( true );
    }

    public void testExcelReaderObject() throws Exception
    {
		ExcelReader reader = new ExcelReader("src/test/resources/login.xls");

		User user = (User) reader.readObject(User.class, 1);
		System.out.println("Name:"+user.getName()+" Password:"+user.getPassword());
		assertTrue( true );
    }
}
