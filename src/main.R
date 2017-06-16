#!/usr/bin/Rscript
# 
# 
# Dependencies:
# install.packages("data.table")

library(data.table)

csv.data <- fread("../resources/travistorrent_8_2_2017.csv")
# csv.data <- read.table("../resources/travistorrent_8_2_2017.csv")

# Vector containing all the teams
teams <- unique(csv.data$gh_project_name)

####################################################################################################### Figures for Exploratory Analysis #######################
pdf("../submission/figures/exploratory_figure.pdf")

# Setup Graph
num.rows = 2
num.cols = 2
par(mfrow=c(num.rows,num.cols))


# Top Left Histogram
hist(csv.data$gh_team_size, breaks=1000,xlab="Team Size", main="Histogram of Team Size")

# Top Right Bargraph
unique.languages <- t(as.data.frame(c(
length(which(csv.data$gh_lang == "ruby")),
length(which(csv.data$gh_lang == "java")),
length(which(csv.data$gh_lang == "javascript")))))
colnames(unique.languages) <- unique(csv.data$gh_lang)
rownames(unique.languages) <- "count"
barplot(unique.languages, main="Unique Languages", ylab="Count", xlab ="Language")

# Bottom Left Density Plot
plot(density(csv.data$gh_test_cases_per_kloc),
	main="Density Of Test Cases/KLOC", xlim=c(1,1000))

# Bottom Right Plot
hist(csv.data$gh_sloc, col=c(rgb(0,0,0,0.2)),breaks=10000,
	main="Histogram of LOC")

dev.off()
####################### Figures for Exploratory Analysis #######################
################################################################################


##### MISC #####
# One Random Team
#sample.team <- teams[1]
#sample.team.data <- csv.data[which(csv.data$gh_project_name == sample.team)]
#sample.team.id <- unique(sample.team.data$tr_build_id)
#plot(sample.team.data$gh_files_added)
