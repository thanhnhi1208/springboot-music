package com.nhi.libary.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.nhi.libary.model.Playlist;
import com.nhi.libary.model.Shareable;
import com.nhi.libary.model.Track;
import com.nhi.libary.model.User;
import com.nhi.libary.repository.PlaylistRepository;
import com.nhi.libary.repository.ShareableRepository;
import com.nhi.libary.repository.TrackRepository;
import com.nhi.libary.repository.UserRepository;

@Service
public class ShareService {

	@Autowired
	private ShareableRepository shareableRepository;
	
	@Autowired
	private TrackRepository trackRepository;
	
	@Autowired
	private PlaylistRepository playlistRepository;
	
	@Autowired
	private UserRepository userRepository;;

	public void shareTrackOrPlaylist(String trackTitle, String playlistTitle, String shareMessage, String email) {
		Track track = this.trackRepository.findByTrackTitle(trackTitle);
		Playlist playlist = this.playlistRepository.findByPlaylistTitle(playlistTitle);
		
		User sharer = this.userRepository.findByEmail(email);
		
		Shareable shareable = new Shareable();
		shareable.setSharers(sharer);
		shareable.setShareDate(LocalDate.now());
		if(track != null) {
			shareable.setTrack(track);
		}else {
			shareable.setPlaylist(playlist);
		}
		
		if(shareMessage != null && shareMessage.equals("") == false) {
			shareable.setShareMessage(shareMessage);
		}
		
		this.shareableRepository.save(shareable);
		
	}

	public List<Shareable> getShareableByArtist(String artistName) {
		User sharer = this.userRepository.findByArtistName(artistName);
		return this.shareableRepository.findBySharer(sharer.getId());
	}

	public List<Shareable> deleteShareable( Long id, Authentication authentication) {
		this.shareableRepository.deleteById(id);
		
		User sharer = this.userRepository.findByEmail(authentication.getName());
		return this.shareableRepository.findBySharer(sharer.getId());
	}
}
