#!/usr/bin/Rscript
# 
# 
# Dependencies:

library(data.table)
library(stats)


csv.data <- fread("../resources/travistorrent_8_2_2017.csv")

# Vector containing all the teams
teams <- unique(csv.data$gh_project_name)

# Extract First team
sample.team <- teams[3]
team.data <- csv.data[which(csv.data$gh_project_name == sample.team)]

team.tests.time <- team.data$gh_first_commit_created_at	# 14
team.tests.failed <- as.numeric(team.data$tr_log_num_tests_failed) #56

team.df <- team.data[,c(14, 56)]
team.df$tr_log_num_tests_failed <- as.numeric(team.df$tr_log_num_tests_failed)
team.df$gh_first_commit_created_at <- as.Date(team.df$gh_first_commit_created_at)
team.df <- team.df[complete.cases(team.df),]


pdf("../submission/figures/q3_team3_exploratory_fig.pdf")
points(team.df$gh_first_commit_created_at,team.df$tr_log_num_tests_failed, pch=19, col=c(rgb(0,0,0,0.1)), xlab="Time", ylab="TR Num Tests Failed")
dev.off()






#ggplot(team.df, aes(team.df$gh_first_commit_created_at, team.df$tr_log_num_tests_failed), coord_cartesian(ylim=c(1,10)) )


