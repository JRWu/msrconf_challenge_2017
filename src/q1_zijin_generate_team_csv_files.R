#!/usr/bin/Rscript

library(data.table)
library(stats)

# Load Master CSV File
csv.data <- fread("../resources/travistorrent_8_2_2017.csv")

# Pulls out all the team names
teams <- unique(csv.data$gh_project_name)

# Create a "List of Lists" of the unique teams
team.data <- list()
for (i in 1:length(unique(teams)))
{
	# Save the Team Name
	temp.team <- teams[i]
	print(paste("Exporting: ", teams[i], sep=""))

	# Extract the Team into its own index in the list team.data
	# This step is slow! Splits into all 1283 teams
	team.data[[i]] <- csv.data[which(csv.data$gh_project_name == temp.team)]

	team.data[[i]] <- team.data[[i]][,c("gh_project_name","gh_team_size","git_num_all_built_commits","git_diff_src_churn","git_diff_test_churn","gh_sloc","gh_test_lines_per_kloc","gh_test_cases_per_kloc","gh_asserts_cases_per_kloc","gh_build_started_at","build_successful")]

	# Remove all NAs
	team.data[[i]] <- team.data[[i]][complete.cases(team.data[[i]]),]
	team.data[[i]] <- unique(team.data[[i]])

	team.name <- gsub("/","", temp.team)
	
	write.table(team.data[[i]], paste("../resources/teams/",team.name,".csv",sep=""),sep=",", quote=F, row.names=F)
}
