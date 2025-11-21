package com.nhi.libary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.nhi.libary.model.ListeningHistory;
import com.nhi.libary.model.Playlist;
import com.nhi.libary.model.SearchHistory;
import com.nhi.libary.model.Track;
import com.nhi.libary.model.User;
import com.nhi.libary.repository.PlaylistRepository;
import com.nhi.libary.repository.SearchHistoryRepository;
import com.nhi.libary.repository.TrackRepository;
import com.nhi.libary.repository.UserRepository;

@Service
public class SearchHistoryService {

	@Autowired
	private SearchHistoryRepository searchHistoryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TrackRepository trackRepository;
	
	@Autowired
	private PlaylistRepository playlistRepository;
	
	public List<SearchHistory> saveSearchHistory(String trackTitle, String playlistTitle, String artistName, Authentication authentication) {
		Track track = null;
		Playlist playlist = null;
		User artist = null;
		
		User searcher = this.userRepository.findByEmail(authentication.getName());
		
		if(trackTitle != null) {
			track = this.trackRepository.findByTrackTitle(trackTitle);
			SearchHistory searchHistory = this.searchHistoryRepository.findBySearcherAndTrack(searcher, track);
			if(searchHistory != null) {
				this.searchHistoryRepository.deleteById(searchHistory.getId());
			}
		}else if(playlistTitle != null) {
			playlist = this.playlistRepository.findByPlaylistTitle(playlistTitle);
			SearchHistory searchHistory = this.searchHistoryRepository.findBySearcherAndPlaylist(searcher, playlist);
			if(searchHistory != null) {
				this.searchHistoryRepository.deleteById(searchHistory.getId());
			}
		}else {
			artist = this.userRepository.findByArtistName(artistName);
			SearchHistory searchHistory = this.searchHistoryRepository.findBySearcherAndUser(searcher, artist);
			
			if(searchHistory != null) {
				this.searchHistoryRepository.deleteById(searchHistory.getId());
			}
			
		}
		
		SearchHistory searchHistory = new SearchHistory();
		searchHistory.setSearcher(searcher);
		searchHistory.setTrack(track);
		searchHistory.setPlaylist(playlist);
		searchHistory.setUser(artist);
		
		this.searchHistoryRepository.save(searchHistory);
		
		return this.searchHistoryRepository.findBySearcher(searcher.getId());
	}


	public List<SearchHistory> findByUser(Authentication authentication) {
		return this.searchHistoryRepository.findBySearcher(this.userRepository.findByEmail(authentication.getName()).getId());
	}


	public List<SearchHistory> deleteSearchHistory(String trackTitle, String playlistTitle, String artistName,
			Authentication authentication) {
		Track track = null;
		Playlist playlist = null;
		User artist = null;
		
		User searcher = this.userRepository.findByEmail(authentication.getName());
		
		if(trackTitle != null) {
			track = this.trackRepository.findByTrackTitle(trackTitle);
			SearchHistory searchHistory = this.searchHistoryRepository.findBySearcherAndTrack(searcher, track);
			if(searchHistory != null) {
				this.searchHistoryRepository.deleteById(searchHistory.getId());
			}
		}else if(playlistTitle != null) {
			playlist = this.playlistRepository.findByPlaylistTitle(playlistTitle);
			SearchHistory searchHistory = this.searchHistoryRepository.findBySearcherAndPlaylist(searcher, playlist);
			if(searchHistory != null) {
				this.searchHistoryRepository.deleteById(searchHistory.getId());
			}
		}else {
			artist = this.userRepository.findByArtistName(artistName);
			SearchHistory searchHistory = this.searchHistoryRepository.findBySearcherAndUser(searcher, artist);
			if(searchHistory != null) {
				this.searchHistoryRepository.deleteById(searchHistory.getId());
			}
			
		}

		return this.searchHistoryRepository.findBySearcher(searcher.getId());
	}


	public List<SearchHistory> deleteAllSearchHistory(Authentication authentication) {
		User searcher = this.userRepository.findByEmail(authentication.getName());
		List<SearchHistory> searchHistoryList = this.searchHistoryRepository.findBySearcher(searcher.getId());
		
		if(searchHistoryList != null && !searchHistoryList.isEmpty()) {
			for (SearchHistory searchHistory : searchHistoryList) {
				this.searchHistoryRepository.deleteById(searchHistory.getId());
			}
		}
		
		return this.searchHistoryRepository.findBySearcher(searcher.getId());
	}
}
