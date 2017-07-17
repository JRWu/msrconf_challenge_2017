#!/usr/bin/Rscript
# Exploratory work for 1 team
library(data.table)
library(stats)
library(ALDEx2)

csv.data <- fread("../resources/travistorrent_8_2_2017.csv")
teams <- unique(csv.data$gh_project_name)


pass.fit <- list()
fail.fit <- list()

pass.slope <- integer()
fail.slope <- integer()

for (j in 1:length(teams))
{
	print(paste("Processed: ",j,"/",length(teams),sep=""))
	temp.team <- teams[j]
	team.name <- gsub("/","", temp.team)
	fig.name <- paste("../resources/figures/pfTrends/pass_fail_fig",team.name,".png",sep="")

	tdata <- csv.data[which(csv.data$gh_project_name == temp.team)]
	# See if the pass/fail changes as a function of time

	# Determine the rows with the SAME tr_build_id
	dupe.tr_build_id <-  tdata[duplicated(tdata$tr_build_id),]

	build.ids <- unique(tdata$tr_build_id)

	npass <- integer()
	nfail <- integer()
	nerr <- integer()
	ncancel <- integer()
	for (i in 1:length(build.ids))
	{
		ind <- which(tdata$tr_build_id == build.ids[i])
		n.pass <- length(which((tdata[ind,]$tr_status == "passed") == TRUE))
		n.fail <- length(which((tdata[ind,]$tr_status == "failed") == TRUE))
		n.err <- length(which((tdata[ind,]$tr_status == "errored") == TRUE))
		n.canc <- length(which((tdata[ind,]$tr_status == "canceled") == TRUE))

		npass <- c(npass, n.pass)
		nfail <- c(nfail, n.fail)
		nerr <- c(nerr, n.err)
		ncancel <- c(ncancel, n.canc)
	}

	x.vector <- 1:length(npass)

	n.dataframe <- as.data.frame(cbind(npass,nfail,nerr,ncancel,x.vector))
	colnames(n.dataframe) <- c("passed", "failed", "errored", "cancelled", "index")

	png(fig.name, width=600, height=600)
	# Passed GREEN
	plot(npass,col=c(rgb(0,1,0,0.5)), pch = 19, xaxs="i",main=paste(teams[j],": Pass vs Fail",sep=""),xlab="tr_build_id",ylab="# Pass or Fail")
	pass.fit[[j]] <- lm(n.dataframe$passed ~ n.dataframe$index)
	abline(pass.fit,col=c(rgb(0,1,0,1)))
	lines(predict(loess(n.dataframe$passed ~ n.dataframe$index)), col=c(rgb(0,1,0,1)))

	# Failed RED
	points(nfail, col=c(rgb(1,0,0,0.1)), pch = 19)
	fail.fit[[j]] <- lm(n.dataframe$failed ~ n.dataframe$index)
	abline(fail.fit,col=c(rgb(1,0,0,1)))
	lines(predict(loess(n.dataframe$failed ~ n.dataframe$index)), col=c(rgb(1,0,0,1)))
	dev.off()

	pass.slope <- c(pass.slope, pass.fit[[j]]$coefficients[[2]])
	fail.slope <- c(fail.slope, fail.fit[[j]]$coefficients[[2]])
}

# Look at the Delta change
slope.df <- as.data.frame(cbind(pass.slope,fail.slope))
colnames(slope.df) <- c("pass_slope", "fail_slope")
write.table(slope.df, file="pass_fail_lm.txt", sep="\t",quote=F)

which(slope.df$pass_slope > 0 & slope.df$fail_slope < 0)
which(slope.df$pass_slope < 0 & slope.df$fail_slope > 0)


fp.filename <- paste("../submission/figures/jia_q3_pass_fail_coefficients.png")
png(fp.filename, width=600,height=600)
plot(slope.df$pass_slope, slope.df$fail_slope, xlim=c(-1,1.5), ylim=c(-0.5,1), main="Fail vs Pass Coefficients", ylab="Failure Coefficient", xlab="Pass Coefficient", pch=19, col=c(rgb(0,0,0,0.2)))
abline(0,0)
abline(h=0,v=0)


par(new=TRUE, oma=c(3,1,3,2))
layout(matrix(1:4,3))
plot(slope.df$pass_slope, slope.df$fail_slope, ylab="",xlab="",ylim=c(-0.1,0.1),xlim=c(-0.1,0.1), pch=19, col=c(rgb(0,0,0,0.2)))
abline(0,0)
abline(h=0,v=0)
dev.off()


# Error BLACK
#points(nerr, col=c(rgb(0,0,0,0.1)), pch = 19)
#abline(lm(n.dataframe$errored ~ n.dataframe$index),col=c(rgb(0,0,0,0.5)))
#lines(predict(loess(n.dataframe$errored ~ n.dataframe$index)), col=c(rgb(0,0,0,0.5)))

# Cancelled YELLOW
#points(ncancel, col=c(rgb(1,1,0,0.5)), pch = 19)
#abline(lm(n.dataframe$errored ~ n.dataframe$index),col=c(rgb(1,1,0,0.5)))
#lines(predict(loess(n.dataframe$errored ~ n.dataframe$index)), col=c(rgb(1,1,0,0.5)))


# Boxplot Figure
slope.df <- read.table("pass_fail_lm.txt")
colnames(slope.df) <- c("Pass Coefficient", "Fail Coefficient")
slope.df.figure.filename <- "../submission/figures/jia_q3_regressionslopes_boxplot.png"

png(slope.df.figure.filename, width=600,height=600)
boxplot(slope.df, ylim=c(-1,2), main="Regression Slopes", ylab="Slope")
dev.off()


# Calculate Stats table
slope.stats.df <- c(
	length(which(slope.df$pass_slope >= 0)),
	length(which(slope.df$fail_slope <= 0)),
	length(which(slope.df$pass_slope > 0 & slope.df$fail_slope > 0)),
	length(which(slope.df$pass_slope > 0 & slope.df$fail_slope < 0)),
	length(which(slope.df$pass_slope < 0 & slope.df$fail_slope > 0)),
	length(which(slope.df$pass_slope < 0 & slope.df$fail_slope < 0))
)
