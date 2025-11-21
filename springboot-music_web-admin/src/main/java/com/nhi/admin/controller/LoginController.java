package com.nhi.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhi.libary.model.Track;
import com.nhi.libary.repository.TrackRepository;
import com.nhi.libary.service.PlaylistService;
import com.nhi.libary.service.TrackService;

@Controller
public class LoginController {
	
	@Autowired
	private TrackService trackService;
	
	@Autowired
	private PlaylistService playlistService;
	
	@Autowired
	private TrackRepository trackRepo;


	@GetMapping("/login")
	public String login(Authentication authentication, Model model) {
		if(authentication != null) {
			return "redirect:/index";			
		}
		
		model.addAttribute("title", "Login Page");
		return "login";
	}
	
	@GetMapping("/index")
	public String index(Model model) {
		model.addAttribute("title", "Home Page");
		long totalSongs = trackService.getTotalTracks();
	    model.addAttribute("totalSongs", totalSongs);
	    long totalAlbum = playlistService.countTotalAlbums();
	    model.addAttribute("totalAlbums", totalAlbum);
	    
	  //thanh nhi thêm
	  		Long totalViews = trackRepo.getTotalViews();

	  	    if (totalViews == null) totalViews = 0L; // tránh bị null

	  	    model.addAttribute("totalViews", totalViews);
	  	    
	  	  List<Track> topTracks = trackService.getTop5Tracks();

	      List<String> titles = topTracks.stream().map(Track::getTrackTitle).toList();
	      List<Long> listens = topTracks.stream().map(Track::getNumberOfListens).toList();

	      model.addAttribute("topTitles", titles);
	      model.addAttribute("topListens", listens);
		return "index";
	}


	
}
