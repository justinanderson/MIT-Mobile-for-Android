package edu.mit.mitmobile2.news.beans;

import java.util.ArrayList;

import edu.mit.mitmobile2.news.beans.NewsImageRepresentation;

public class NewsImage{
	private String description;
	private String credits;
	private ArrayList<NewsImageRepresentation> representations;
	private int min_index = -1;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
	public ArrayList<NewsImageRepresentation> getRepresentations() {
		return representations;
	}
	public NewsImageRepresentation getSmallestRepresentationsByDiagonal() {
		if(this.min_index == -1){
			int mIndex = 0;
			double min_size = 10000;
			double tmp_size = 0;
			int i = 0;
			for(NewsImageRepresentation ir : representations){
				tmp_size = Math.sqrt(ir.getHeight()^2 + ir.getWidth()^2); 
				if(tmp_size < min_size){
					min_size = tmp_size;
					mIndex = i; 
				}
				i++;
			}
			this.min_index = mIndex;
		}
		return representations.get(this.min_index);
	}
	public NewsImageRepresentation getRepresentationBestFitByWidth(int width) {
		int mIndex = 0;
		double max_size = 0;
		int i = 0;
		for(NewsImageRepresentation ir : representations){
			if(ir.getWidth() > max_size && ir.getWidth() < width){
				max_size = ir.getWidth();
				mIndex = i; 
			}
			i++;
		}
		return representations.get(mIndex);
	}
	public void setRepresentations(ArrayList<NewsImageRepresentation> representations) {
		this.representations = representations;
	}
	
}