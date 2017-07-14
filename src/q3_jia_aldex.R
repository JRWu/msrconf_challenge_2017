#!/usr/bin/Rscript
# 
# RQ3: Does the use of CI lead to higher-quality products, for example by catching bugs and test failures earlier?
# 
# Dependencies:
# install.packages("data.table")
# install.packages("stats")
# source("https://bioconductor.org/biocLite.R")
# biocLite("ALDEx2") 

library(data.table)
library(stats)
library(ALDEx2)

# Load Master CSV File
csv.data <- fread("../resources/travistorrent_8_2_2017.csv")

# Vector containing all the team names
teams <- unique(csv.data$gh_project_name)

# Create a "List of Lists" of the unique teams
team.data <- list()
print("Preprocessing the Datasets:")
for (i in 1:length(unique(teams)))
{
	# Save the Team Name
	temp.team <- teams[i]

	# Extract the Team into its own index in the list team.data
	# This step is slow! Splits into all 1283 teams
	team.data[[i]] <- csv.data[which(csv.data$gh_project_name == temp.team)]
	team.data[[i]] <- team.data[[i]][,c("gh_team_size", "tr_log_num_tests_failed","gh_first_commit_created_at")]

	# Preprocess Data
	team.data[[i]]$tr_log_num_tests_failed <- as.numeric(team.data[[i]]$tr_log_num_tests_failed)
	team.data[[i]]$gh_team_size <- as.numeric(team.data[[i]]$gh_team_size)
	team.data[[i]]$gh_first_commit_created_at <- as.Date(team.data[[i]]$gh_first_commit_created_at)

	# Remove all NAs
	team.data[[i]] <- team.data[[i]][complete.cases(team.data[[i]]),]
	team.data[[i]] <- unique(team.data[[i]])

	# Simple Linear Regression 
	if (nrow(team.data[[i]]) != 0)
	{
		x.vector <- 1:length(team.data[[i]]$tr_log_num_tests_failed)
		team.data[[i]] <- cbind(team.data[[i]], x.vector)
	}
}
print("Preprocessing the Datasets: COMPLETE")


################################################################################
################################### LM FIGURE ##################################
################################################################################
# Callback for "linear model", or simple linear regression as f'n of Time
team.lm <- lapply(team.data,function(x){
	if(nrow(x) != 0){
		lm(x$tr_log_num_tests_failed ~ x$x.vector, data=x) 
	}
})
# Calculate the Proportion of Positive Slopes
prop.positive <- lapply(team.lm, function(x){
	if(!is.null(x)){
		return (x$coefficients[[2]] > 0)
	}
})
# Calculate the Proportion of Negative Slopes
prop.negative <- lapply(team.lm, function(x){
	if(!is.null(x)){
		return (x$coefficients[[2]] < 0)
	}
})
# Calculate the Proportion of Zero-length Slopes
prop.zero <- lapply(team.lm, function(x){
	if(!is.null(x)){
		return (x$coefficients[[2]] == 0)
	}
})

# Barplot of the Results of Simple LM across tr_log_num_tests_failed
p.all <- t(as.data.frame(c(
	length(which(unlist(prop.negative) == TRUE)),	#473
	length(which(unlist(prop.positive) == TRUE)),	#414
	length(which(unlist(prop.zero) == TRUE)) 		#158
													#1045
)))
colnames(p.all) <- c("Negative", "Positive", "Zero")
barplot(p.all, main = "Linear Model Proportion", ylab="Count", xlab="Sign")
################################################################################
################################### LM FIGURE ##################################
################################################################################





# Experimental Work
team.sizes <- lapply(team.data,function(x){mean(x$gh_team_size)})
team.rowcount <- lapply(team.data,function(x){nrow(x)})
mean.failures.per.team <- lapply(team.data,function(x){mean(x$tr_log_num_tests_failed)})

team.sizes <- unlist(team.sizes)
team.rowcount <- unlist(team.rowcount)
mean.failures.per.team <- unlist(mean.failures.per.team)


smfpt <- ceiling(mean.failures.per.team/team.sizes)
smfpt <- smfpt[complete.cases(smfpt)]


plot(team.rowcount[2:length(team.rowcount)])
plot(mean.failures.per.team[2:length(mean.failures.per.team)])
