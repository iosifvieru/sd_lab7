#!/bin/bash


path=$PWD
nr=$1

echo "==== START SCRIPT ===="

# logger
java -jar $path/out/artifacts/LoggerMicroservice_jar/LoggerMicroservice.jar & 

# pornire message processor

java -jar $path/out/artifacts/MessageProcessorMicroservice_jar/MessageProcessorMicroservice.jar &
sleep 0.5

# bidding processor

java -jar $path/out/artifacts/BiddingProcessorMicroservice_jar/BiddingProcessorMicroservice.jar &
sleep 0.5

# auctioneer

java -jar $path/out/artifacts/AuctioneerMicroservice_jar/AuctioneerMicroservice.jar &
sleep 0.5

# bidder

for ((i = 0; i <= nr; i++))
do
	java -jar $path/out/artifacts/BidderMicroservice_jar/BidderMicroservice.jar &
done
