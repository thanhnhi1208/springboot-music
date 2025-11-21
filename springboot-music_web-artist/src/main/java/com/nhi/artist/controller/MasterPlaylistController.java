package com.nhi.artist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nhi.libary.model.MasterPlaylist;
import com.nhi.libary.model.Playlist;
import com.nhi.libary.service.MasterPlaylistService;

@Controller
public class MasterPlaylistController {

	@Autowired
	private MasterPlaylistService masterPlaylistService;
	
	@GetMapping("/masterPlaylist/findAllMasterPlaylist")
	@ResponseBody
	public List<MasterPlaylist> findAllMasterPlaylist(){
		return this.masterPlaylistService.findAllMasterPlaylistInArtist();
	}
	
	@GetMapping("/masterPlaylist/findPlaylistOfMasterPlaylist")
	@ResponseBody
	public List<Playlist> findPlaylistOfMasterPlaylist(Long id){
		return this.masterPlaylistService.findPlaylistOfMasterPlaylist(id);
	}
}
