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

	team.data[[i]] <- team.data[[i]][,c("gh_first_commit_created_at","git_num_all_built_commits", "gh_project_name", "gh_team_size", "gh_sloc", "gh_test_lines_per_kloc", "gh_test_cases_per_kloc", "gh_asserts_cases_per_kloc", "tr_status")]

	# Remove all NAs
	team.data[[i]] <- team.data[[i]][complete.cases(team.data[[i]]),]

	team.name <- gsub("/","", team.name)
	
	write.table(team.data[[i]], paste("../resources/",team.name,".csv",sep=""),sep=",", quote=F, row.names=F)
}



	# At this point, the teams can be accessed by:
	# team.data[[x]], where x is the index in the range [1:1283]
	# i.e team.data[[1]] will give you the data of the first team


# Sample Extraction for Team 1
# Subset out the range of team data values:
# mini = [0,25]
# small = [26,50]
# medium = [51,200]
# large = [201+]
team.data.mini <- team.data[[1]][team.data[[1]]$gh_team_size <= 25,]

team.data.small <- team.data[[1]][team.data[[1]]$gh_team_size > 25 & team.data[[1]]$gh_team_size <= 50,]

team.data.medium <- team.data[[1]][team.data[[1]]$gh_team_size > 50 & team.data[[1]]$gh_team_size <= 200,]

team.data.large <- team.data[[1]][team.data[[1]]$gh_team_size > 200,]

# For-loops are generally discouraged in R because they're so resource-intensive
# Typically, the "which" keyword is used, and you can specify some condition for it to subset out rows 
# So now all the data is subsetted into mini, small, medium, and large classifiers
# I will now use these subsets to extract something:

# i.e. If I wanted to ask: How many commits for the medium data subset have a $tr_log_num_tests_failed value of greater than 1000 (i.e. how many commits have more than 1000 errors)
# I would type:
rows.of.failed.commits <- which(team.data.medium$tr_log_num_tests_failed > 1000)
# Data in R is indexed by: data.frame.name[x.rows, y.rows]
# Subsequently, if I want to extract those rows out to do some meaningful work on, then I can say
team.data.medium.failed.commits <- team.data.medium[rows.of.failed.commits,]

# This operation removes the necessity of iterating over the rows; now you have a subset of data where each row represents a commit that has over 1000 errors and you can do something like look at the eensity distribution of the failed tests that are greater than 1000
plot(density(team.data.medium.failed.commits$tr_log_num_tests_failed))
