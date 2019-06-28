package com.example.jimmy.weatherforecast;

import com.example.jimmy.weatherforecast.net.NetworkUtil;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {
    
    @Test
    public void testBuildUri() {
        URL fakeUrl = null;
        try {

            fakeUrl = new URL("http://api.openweathermap.org/data/2.5/forecast?zip=55104&appid=66130d1293395426fac6b9e2d9299e8e");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals("asdfasdfsd", NetworkUtil.getReponseFromUri(fakeUrl));



}

}