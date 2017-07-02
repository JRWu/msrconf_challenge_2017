#!/usr/bin/Rscript
# 
# 
# Dependencies:

library(data.table)
library(stats)

# Load Master CSV File
csv.data <- fread("../resources/travistorrent_8_2_2017.csv")

# Condense tr_build_id into only the unique build id's (DOESNT WORK EXPLAIN TO J WHY NOT)
# unique.val <- unique(csv.data$tr_build_id)
# csv.data.filtered <- csv.data[unique.val,]
# uncomment later
# csv.data <- csv.data.filtered

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

# Add 0 for NA values, assume 0 tests failed on those builds
rows.with.NA <- which(is.na(team.df$tr_log_num_tests_failed), arr.ind=TRUE)
team.df$tr_log_num_tests_failed[rows.with.NA] <- 0

team.df <- team.df[complete.cases(team.df),]

#pdf("../submission/figures/q3_team3_exploratory_fig.pdf")
plot(team.df$gh_first_commit_created_at,team.df$tr_log_num_tests_failed, pch=19, col=c(rgb(0,0,0,0.1)), xlab="Time", ylab="TR Num Tests Failed")
#dev.off()

#ggplot(team.df, aes(team.df$gh_first_commit_created_at, team.df$tr_log_num_tests_failed), coord_cartesian(ylim=c(1,10)) )

# Linear Regression Work
x.vector <- 1:length(team.df$tr_log_num_tests_failed)
team.df <- cbind(team.df, x.vector)

regression.line <-lm(team.df$tr_log_num_tests_failed ~ team.df$x.vector, data=team.df) 
abline(regression.line$coefficients[[1]],regression.line$coefficients[[2]])




