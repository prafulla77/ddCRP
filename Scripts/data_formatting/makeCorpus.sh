#!/bin/bash

#Utility script which converts the venue observations files of each city into a single file with one line for each city. The output is a corpus file in the Data/ directory.

for filename in ../../../../livehoods_data/city_clustering/hierarchical_ddCRP_new/Data/cities_sim/city_*_venue_observations.txt
do
	tr "\n" " " < $filename >> ../../Data/corpus_temp.txt
	echo "<DELIM>" >> ../../Data/corpus_temp.txt
done

#Removing the "<DELIM>" string
tr -d "<DELIM>" < ../../Data/corpus_temp.txt > ../../Data/corpus.txt
rm ../../Data/corpus_temp.txt

