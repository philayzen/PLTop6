package pltop6.test;

import org.junit.Test;

import pltop6.java.*;

import org.junit.Assert;

public class UtilsTest {
	@Test
	public void convertYearToSeasonTest() {
		Assert.assertEquals("91/92", Utils.convertYearToSeason(2091));
		Assert.assertEquals("23/24", Utils.convertYearToSeason(2023));
		Assert.assertEquals("00/01", Utils.convertYearToSeason(2000));
		Assert.assertEquals("99/00", Utils.convertYearToSeason(1999));
		Assert.assertEquals("97/98", Utils.convertYearToSeason(1997));
		Assert.assertEquals("92/93", Utils.convertYearToSeason(1992));
	}
	@Test
	public void convertSeasonToYearTest() {
		Assert.assertEquals(2091, Utils.convertSeasonToYear("91/92"));
		Assert.assertEquals(2010, Utils.convertSeasonToYear("10/11"));
		Assert.assertEquals(2000, Utils.convertSeasonToYear("00/01"));
		Assert.assertEquals(1999, Utils.convertSeasonToYear("99/00"));
		Assert.assertEquals(1995, Utils.convertSeasonToYear("95/96"));
		Assert.assertEquals(1992, Utils.convertSeasonToYear("92/93"));
	}
	
}
