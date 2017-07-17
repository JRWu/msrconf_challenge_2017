#!/usr/bin/Rscript

library(data.table)
library(stats)

# setup
pdf("../submission/figures/q1_zijin_team_size_figures.pdf")

# team size distribution by records
df <- fread("../resources/results/team_size_records.csv")
hist(df$entry, breaks=1000, xlab="Team Size", ylab="Frequency", main="Team Size Distribution Per Record")

# team size distribution by projects
df <- fread("../resources/results/team_size_projects.csv")
hist(df$entry, breaks=1000, xlab="Team Size", ylab="Frequency", main="Team Size Distribution Per Project")

# num_commits
df <- fread("../resources/results/num_commits.csv")
plot(df$team_size, df$entry, xlab="Team Size", ylab="Number Of Commits Built Per Hour", main="Number Of Commits Built Per Hour By Team Size")
liner.model <- lm(df$entry ~ df$team_size, data=df)
abline(liner.model)

mini <- fread("../resources/results/num_commits_mini.csv")
small <- fread("../resources/results/num_commits_small.csv")
medium <- fread("../resources/results/num_commits_medium.csv")
large <- fread("../resources/results/num_commits_large.csv")
df <- c(mini, small, medium, large)
boxplot(df, ylab="Number Of Commits Built Per Hour", main="Number Of Commits Built Per Hour By Team Category")

# src_churn
df <- fread("../resources/results/src_churn.csv")
plot(df$team_size, df$entry, xlab="Team Size", ylab="SRC Churn Per Hour", main="SRC Churn Per Hour By Team Size")
liner.model <- lm(df$entry ~ df$team_size, data=df)
abline(liner.model)

mini <- fread("../resources/results/src_churn_mini.csv")
small <- fread("../resources/results/src_churn_small.csv")
medium <- fread("../resources/results/src_churn_medium.csv")
large <- fread("../resources/results/src_churn_large.csv")
df <- c(mini, small, medium, large)
boxplot(df, ylab="SRC Churn Per Hour", main="SRC Churn Per Hour By Team Category")

# test_churn
df <- fread("../resources/results/test_churn.csv")
plot(df$team_size, df$entry, xlab="Team Size", ylab="Test Churn Per Hour", main="Test Churn Per Hour By Team Size")
liner.model <- lm(df$entry ~ df$team_size, data=df)
abline(liner.model)

mini <- fread("../resources/results/test_churn_mini.csv")
small <- fread("../resources/results/test_churn_small.csv")
medium <- fread("../resources/results/test_churn_medium.csv")
large <- fread("../resources/results/test_churn_large.csv")
df <- c(mini, small, medium, large)
boxplot(df, ylab="Test Churn Per Hour", main="Test Churn Per Hour By Team Category")

# sloc
df <- fread("../resources/results/sloc.csv")
plot(df$team_size, df$entry, xlab="Team Size", ylab="SLOC Added Per Hour", main="SLOC Added Per Hour By Team Size")
liner.model <- lm(df$entry ~ df$team_size, data=df)
abline(liner.model)

mini <- fread("../resources/results/sloc_mini.csv")
small <- fread("../resources/results/sloc_small.csv")
medium <- fread("../resources/results/sloc_medium.csv")
large <- fread("../resources/results/sloc_large.csv")
df <- c(mini, small, medium, large)
boxplot(df, ylab="SLOC Added Per Hour", main="SLOC Added Per Hour By Team Category")

# test_lines_dens
df <- fread("../resources/results/test_lines_dens.csv")
plot(df$team_size, df$entry, xlab="Team Size", ylab="Test Lines Per KLOC", main="Test Lines Per KLOC By Team Size")
liner.model <- lm(df$entry ~ df$team_size, data=df)
abline(liner.model)

mini <- fread("../resources/results/test_lines_dens_mini.csv")
small <- fread("../resources/results/test_lines_dens_small.csv")
medium <- fread("../resources/results/test_lines_dens_medium.csv")
large <- fread("../resources/results/test_lines_dens_large.csv")
df <- c(mini, small, medium, large)
boxplot(df, ylab="Test Lines Per KLOC", main="Test Lines Per KLOC By Team Category")

# test_cases_dens
df <- fread("../resources/results/test_cases_dens.csv")
plot(df$team_size, df$entry, xlab="Team Size", ylab="Test Cases Per KLOC", main="Test Cases Per KLOC By Team Size")
liner.model <- lm(df$entry ~ df$team_size, data=df)
abline(liner.model)

mini <- fread("../resources/results/test_cases_dens_mini.csv")
small <- fread("../resources/results/test_cases_dens_small.csv")
medium <- fread("../resources/results/test_cases_dens_medium.csv")
large <- fread("../resources/results/test_cases_dens_large.csv")
df <- c(mini, small, medium, large)
boxplot(df, ylab="Test Cases Per KLOC", main="Test Cases Per KLOC By Team Category")

# asserts_cases_dens
df <- fread("../resources/results/asserts_cases_dens.csv")
plot(df$team_size, df$entry, xlab="Team Size", ylab="Asserts Cases Per KLOC", main="Asserts Cases Per KLOC By Team Size")
liner.model <- lm(df$entry ~ df$team_size, data=df)
abline(liner.model)

mini <- fread("../resources/results/asserts_cases_dens_mini.csv")
small <- fread("../resources/results/asserts_cases_dens_small.csv")
medium <- fread("../resources/results/asserts_cases_dens_medium.csv")
large <- fread("../resources/results/asserts_cases_dens_large.csv")
df <- c(mini, small, medium, large)
boxplot(df, ylab="Asserts Cases Per KLOC", main="Asserts Cases Per KLOC By Team Category")
