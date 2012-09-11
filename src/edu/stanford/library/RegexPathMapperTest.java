package edu.stanford.library;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.regex.Pattern;

import org.akubraproject.map.IdMapper;
import org.junit.Test;
import org.fcrepo.server.storage.lowlevel.akubra.HashPathIdMapper;

public class RegexPathMapperTest {

	@Test
	public void testGoodExternalIdPatterns() {
	    String patternStr = "([a-z]{2})([0-9]{3})([a-z]{2})([0-9]{4})";
	    
	    Pattern pattern = Pattern.compile(patternStr);
	    
		IdMapper mapper = new RegexPathMapper(pattern);
		assertEquals(URI.create("druid:aa000aa0000"), mapper.getExternalId(URI.create("file:druid/aa/000/aa/0000")));
	}

	@Test
	public void testGoodInternalIdPatterns() {
	    String patternStr = "([a-z]{2})([0-9]{3})([a-z]{2})([0-9]{4})";
	    
	    Pattern pattern = Pattern.compile(patternStr);
	    
		IdMapper mapper = new RegexPathMapper(pattern);
		
		assertEquals(URI.create("file:druid/aa/000/aa/0000"), mapper.getInternalId(URI.create("druid:aa000aa0000")));
	}
	
	@Test
	public void testFallbackPatterns() {

	    String patternStr = "([a-z]{2})([0-9]{3})([a-z]{2})([0-9]{4})";
	    
	    Pattern pattern = Pattern.compile(patternStr);

		IdMapper fallbackMapper = new HashPathIdMapper("#");
		
		IdMapper mapper = new RegexPathMapper(pattern, fallbackMapper);
		
		assertEquals(URI.create("file:0/druid%3Aasdfghjk"), mapper.getInternalId(URI.create("druid:asdfghjk")));
	}
	
	@Test
	public void testAppendFullPidPatterns() {
		String patternStr = "([a-z]{2})([0-9]{3})([a-z]{2})([0-9]{4})";
		    
		Pattern pattern = Pattern.compile(patternStr);
		    
	    IdMapper mapper = new RegexPathMapper(pattern, true);
	      

		assertEquals(URI.create("file:druid/aa/000/aa/0000/druid%3Aaa000aa0000"), mapper.getInternalId(URI.create("druid:aa000aa0000")));
		assertEquals(URI.create("druid:aa000aa0000"), mapper.getExternalId(URI.create("file:druid/aa/000/aa/0000/druid%3Aaa000aa0000")));
	}
}
