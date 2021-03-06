package edu.stanford.library;

import java.io.File;
import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.akubraproject.map.IdMapper;
import com.yourmediashelf.fedora.akubra.TrivialIdMapper;

public class RegexPathMapper implements IdMapper {

    private static final String internalScheme = "file";
    private IdMapper fallbackMapper;
	private Pattern pattern;
	private boolean append_full_pid_to_internal_id;
    
    public RegexPathMapper(Pattern pattern) {
        this(pattern, new TrivialIdMapper(), false);
    }
    
    public RegexPathMapper(Pattern pattern, boolean append_full_pid_to_internal_id) {
        this(pattern, new TrivialIdMapper(), append_full_pid_to_internal_id);
    }
    
    public RegexPathMapper(Pattern pattern, IdMapper fallbackMapper) {
        this(pattern, fallbackMapper, false);
    }

    public RegexPathMapper(Pattern pattern, IdMapper fallbackMapper, boolean append_full_pid_to_internal_id) {
    	this.pattern = pattern;
    	this.fallbackMapper = fallbackMapper; 	
        this.append_full_pid_to_internal_id = append_full_pid_to_internal_id;
    }
    
	@Override
	public URI getExternalId(URI internalId) throws NullPointerException {
		String fullPath = internalId.toString().substring(5);
		
		String[] pathComponents = fullPath.split("/");
		
		if(append_full_pid_to_internal_id == true) {
			 int i = fullPath.lastIndexOf('/');
			 return URI.create(decode(fullPath.substring(i + 1)));
		} else {
			StringBuilder builder = new StringBuilder();
			
			builder.append(decode(pathComponents[0]));
			builder.append(":");
			
			for (int i=1; i<pathComponents.length; i++) {
				builder.append(decode(pathComponents[i]));
			}
			
			return URI.create(builder.toString());
		}
	}

	@Override
	public URI getInternalId(URI externalId) throws NullPointerException {
		String uri = externalId.toString();
        
		StringBuilder builder = new StringBuilder();
		
		int ns = uri.indexOf(':');
		
		String namespace = uri.substring(0, ns);
		
		builder.append(encode(namespace));
		
		String value = uri.substring(ns + 1);
		
	    Matcher matcher = pattern.matcher(value);
		
	    boolean matchFound = matcher.find();

	    if (matchFound) {
	        // Get all groups for this match
	        for (int i=1; i<=matcher.groupCount(); i++) {
	            builder.append(File.separator);
	            builder.append(encode(matcher.group(i)));
	        }

			if(append_full_pid_to_internal_id == true) {
				builder.append(File.separator);
				builder.append(encode(uri));
			}

	        return URI.create(internalScheme + ":" + builder.toString());
	        
	    } else {
	    	return fallbackMapper.getInternalId(externalId);
	    }
	    
	}

	@Override
	public String getInternalPrefix(String externalPrefix) throws NullPointerException {
		 return null;
	}

	private static String encode(String uri) {
		try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException wontHappen) {
            throw new RuntimeException(wontHappen);
        } 
    }
	
	private static String decode(String encodedURI) {
	    try {
	        return URLDecoder.decode(encodedURI, "UTF-8");
	    } catch (UnsupportedEncodingException wontHappen) {
	        throw new RuntimeException(wontHappen);
	    }
	}
}
