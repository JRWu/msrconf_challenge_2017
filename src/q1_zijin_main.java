import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Record {
	public String gh_project_name;
	public int gh_team_size;
	public int git_num_all_built_commits;
	public int git_diff_src_churn;
	public int git_diff_test_churn;
	public int gh_sloc;
	public double gh_test_lines_per_kloc;
	public double gh_test_cases_per_kloc;
	public double gh_asserts_cases_per_kloc;
	public String gh_build_started_at;
	public boolean build_successful;
	
	public Record(String str) {
		String[] s = str.split(",");
		
		this.gh_project_name = s[0];
		this.gh_team_size = Integer.parseInt(s[1]);
		this.git_num_all_built_commits = Integer.parseInt(s[2]);
		this.git_diff_src_churn = Integer.parseInt(s[3]);
		this.git_diff_test_churn = Integer.parseInt(s[4]);
		this.gh_sloc = Integer.parseInt(s[5]);
		this.gh_test_lines_per_kloc = Double.parseDouble(s[6]);
		this.gh_test_cases_per_kloc = Double.parseDouble(s[7]);
		this.gh_asserts_cases_per_kloc = Double.parseDouble(s[8]);
		this.gh_build_started_at = s[9];
		this.build_successful = s[10].equals("TRUE") ? true : false;
	}
}

public class q1_zijin_main {
	public static final String FILE_PATH = "../resources/teams/";
	public static final String FILE_OUT_PATH = "../resources/results/";
	public static final String COLUMN_HEADER =
		"gh_project_name," +
		"gh_team_size," +
		"git_num_all_built_commits," +
		"git_diff_src_churn," +
		"git_diff_test_churn," +
		"gh_sloc," +
		"gh_test_lines_per_kloc," +
		"gh_test_cases_per_kloc," +
		"gh_asserts_cases_per_kloc," +
		"gh_build_started_at," +
		"build_successful";
	
	/**
	 * Team category definitions
	 */
	public static final int MINI_START = 0;
	public static final int MINI_END = 25;
	public static final int SMALL_START = 26;
	public static final int SMALL_END = 50;
	public static final int MEDIUM_START = 51;
	public static final int MEDIUM_END = 150;
	public static final int LARGE_START = 151;
	public static final int LARGE_END = 288;
	
	/**
	 * Main
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		column();
		record();
		teamSizeRecords();
		teamSizeProjects();
		
		numCommits();
		srcChurn();
		testChurn();
		sloc();
		testLinesDens();
		testCasesDens();
		assertsCasesDens();
		
		numCommitsTeamChange();
		srcChurnTeamChange();
		testChurnTeamChange();
		slocTeamChange();
		testLinesDensTeamChange();
		testCasesDensTeamChange();
		assertsCasesDensTeamChange();
	}
	
	/**
	 * Helper function, return the second difference between two date strings
	 * @param prev
	 * @param cur
	 * @return
	 * @throws ParseException
	 */
	public static long secDiff(String prev, String cur) throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date prevDate = format.parse(prev);
		Date curDate = format.parse(cur);
		long diffInMillies = curDate.getTime() - prevDate.getTime();
	    return diffInMillies/1000;
	}
	
	/**
	 * Helper function, return the average of a double list
	 * @param list
	 * @return
	 */
	public static double avg(List<Double> list) {
		if (list.isEmpty()) return 0;
		
		double sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i);
		}
		return sum/list.size();
	}
	
	/**
	 * Helper function, return the team category of given team size
	 * @param teamSize
	 * @return
	 */
	public static int getTeamCategory(int teamSize) {
		if (teamSize <= MINI_END) return 0;
		else if (teamSize <= SMALL_END) return 1;
		else if (teamSize <= MEDIUM_END) return 2;
		else return 3;
	}
	
	/**
	 * Check if all the files have the same column header
	 * @throws IOException
	 */
	public static void column() throws IOException {
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	if (!line.equals(COLUMN_HEADER)) {
	        		System.out.println("Error column header:" + file.getName());
	        	}
	        	break;
	        }
		}
		System.out.println("All csv files have correct column header");
	}
	
	/**
	 * Count number of records, get teams with max/min number of records
	 * @throws IOException
	 */
	public static void record() throws IOException {
		int sum = 0;
		int min = Integer.MAX_VALUE;
		int max = 0;
		Set<String> maxTeams = new HashSet<>();
		Set<String> minTeams = new HashSet<>();
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        }
	        // minus one for header line
	        lineCt--;
	        
	        if (lineCt > max) {
	        	max = lineCt;
	        	maxTeams.clear();
	        	maxTeams.add(file.getName());
	        }
	        else if (lineCt == max) {
	        	maxTeams.add(file.getName());
	        }
	        else if (lineCt == min) {
	        	minTeams.add(file.getName());
	        }
	        else if (lineCt < min) {
	        	min = lineCt;
	        	minTeams.clear();
	        	minTeams.add(file.getName());
	        }
	        
	        sum += lineCt;
	    }
		System.out.println("Toatal number of records:" + sum);
		System.out.println("Teams with max records:" + maxTeams + ", count: " + max);
		System.out.println("Teams with min records:" + minTeams + ", count: " + min);
	}
	
	/**
	 * Team size distribution by records
	 * @throws IOException
	 */
	public static void teamSizeRecords() throws IOException {
		List<Integer> sizes = new ArrayList<>();
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record temp = new Record(line);
	        	sizes.add(temp.gh_team_size);
	        }
		}
		
		Collections.sort(sizes);
		int length = sizes.size();
		System.out.println("Min team size per record:" + sizes.get(0));
		System.out.println("Max team size per record:" + sizes.get(length - 1));
		
		// generate csv file for R to draw figures
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "team_size_records.csv"));
		fw.write("entry");
		for (int i = 0; i < length; i++) {
			fw.write("\n" + sizes.get(i));
		}
		fw.close();
	}
	
	/**
	 * Team size distribution by projects
	 * @throws IOException
	 */
	public static void teamSizeProjects() throws IOException {
		List<Integer> sizes = new ArrayList<>();
		int min = Integer.MAX_VALUE;
		int max = 0;
		Set<String> maxTeams = new HashSet<>();
		Set<String> minTeams = new HashSet<>();
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			int curSum = 0;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record temp = new Record(line);
	        	curSum += temp.gh_team_size;
	        }
	        // minus one for header line
	        lineCt--;
	        
	        int curSize = (int) Math.round((double)curSum/lineCt);
	        sizes.add(curSize);
	        
	        if (curSize > max) {
	        	max = curSize;
	        	maxTeams.clear();
	        	maxTeams.add(file.getName());
	        }
	        else if (curSize == max) {
	        	maxTeams.add(file.getName());
	        }
	        else if (curSize == min) {
	        	minTeams.add(file.getName());
	        }
	        else if (curSize < min) {
	        	min = curSize;
	        	minTeams.clear();
	        	minTeams.add(file.getName());
	        }
		}
		
		Collections.sort(sizes);
		int length = sizes.size();
		System.out.println("Total project number:" + length);
		System.out.println("Min average team size per project: " + minTeams + ", team size: " + sizes.get(0));
		System.out.println("Max average team size per project: " + maxTeams + ", team size: " + sizes.get(length - 1));
		
		// generate csv file for R to draw figures
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "team_size_projects.csv"));
		fw.write("entry");
		for (int i = 0; i < length; i++) {
			fw.write("\n" + sizes.get(i));
		}
		fw.close();
	}
	
	/**
	 * Analyze on git_num_all_built_commits
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void numCommits() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Double>> list = new ArrayList<>();
		// team size per record is from 0 to 288
		for (int i = 0; i <= 288; i++) {
			List<Double> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	int curSize = cur.gh_team_size;
	        	if (prev != null &&
	        			cur.gh_team_size == prev.gh_team_size &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		// add an entry to the corresponding team size entry list
	        		// each entry is the number of commits built per hour
	        		list.get(curSize).add(
	        				(double)(cur.git_num_all_built_commits * 3600)/secDiff(
	        						prev.gh_build_started_at, cur.gh_build_started_at));
	        		totalCt++;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		// calculate the average of entries for each team size
		List<Double> result = new ArrayList<>();
		for (int i = 0; i <= 288; i++) {
			result.add(avg(list.get(i)));
		}
		
		// generate csv files for all team sizes and for different team categories
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "num_commits.csv"));
		fw.write("entry,team_size");
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != 0) {
				fw.write("\n" + result.get(i) + "," + i);
			}
		}
		fw.close();
		
		List<Double> mini = new ArrayList<>();
		for (int i = MINI_START; i <= MINI_END; i++) {
			if (result.get(i) != 0) {
				mini.add(result.get(i));
			}
		}
		FileWriter fwMini = new FileWriter(new File(FILE_OUT_PATH + "num_commits_mini.csv"));
		fwMini.write("mini");
		for (int i = 0; i < mini.size(); i++) {
			fwMini.write("\n" + mini.get(i));
		}
		fwMini.close();
		
		List<Double> small = new ArrayList<>();
		for (int i = SMALL_START; i <= SMALL_END; i++) {
			if (result.get(i) != 0) {
				small.add(result.get(i));
			}
		}
		FileWriter fwSmall = new FileWriter(new File(FILE_OUT_PATH + "num_commits_small.csv"));
		fwSmall.write("small");
		for (int i = 0; i < small.size(); i++) {
			fwSmall.write("\n" + small.get(i));
		}
		fwSmall.close();
		
		List<Double> medium = new ArrayList<>();
		for (int i = MEDIUM_START; i <= MEDIUM_END; i++) {
			if (result.get(i) != 0) {
				medium.add(result.get(i));
			}
		}
		FileWriter fwMedium = new FileWriter(new File(FILE_OUT_PATH + "num_commits_medium.csv"));
		fwMedium.write("medium");
		for (int i = 0; i < medium.size(); i++) {
			fwMedium.write("\n" + medium.get(i));
		}
		fwMedium.close();
		
		List<Double> large = new ArrayList<>();
		for (int i = LARGE_START; i <= LARGE_END; i++) {
			if (result.get(i) != 0) {
				large.add(result.get(i));
			}
		}
		FileWriter fwLarge = new FileWriter(new File(FILE_OUT_PATH + "num_commits_large.csv"));
		fwLarge.write("large");
		for (int i = 0; i < large.size(); i++) {
			fwLarge.write("\n" + large.get(i));
		}
		fwLarge.close();
		
		System.out.println("Total number of records used analyzing git_num_all_built_commits: " + totalCt);
	}
	
	/**
	 * Analyze on git_diff_src_churn
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void srcChurn() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Double>> list = new ArrayList<>();
		// team size per record is from 0 to 288
		for (int i = 0; i <= 288; i++) {
			List<Double> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	int curSize = cur.gh_team_size;
	        	if (prev != null &&
	        			cur.gh_team_size == prev.gh_team_size &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		// add an entry to the corresponding team size entry list
	        		// each entry is the src churn per hour
	        		list.get(curSize).add(
	        				(double)(cur.git_diff_src_churn * 3600)/secDiff(
	        						prev.gh_build_started_at, cur.gh_build_started_at));
	        		totalCt++;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		// calculate the average of entries for each team size
		List<Double> result = new ArrayList<>();
		for (int i = 0; i <= 288; i++) {
			result.add(avg(list.get(i)));
		}
		
		// generate csv files for all team sizes and for different team categories
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "src_churn.csv"));
		fw.write("entry,team_size");
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != 0) {
				fw.write("\n" + result.get(i) + "," + i);
			}
		}
		fw.close();
		
		List<Double> mini = new ArrayList<>();
		for (int i = MINI_START; i <= MINI_END; i++) {
			if (result.get(i) != 0) {
				mini.add(result.get(i));
			}
		}
		FileWriter fwMini = new FileWriter(new File(FILE_OUT_PATH + "src_churn_mini.csv"));
		fwMini.write("mini");
		for (int i = 0; i < mini.size(); i++) {
			fwMini.write("\n" + mini.get(i));
		}
		fwMini.close();
		
		List<Double> small = new ArrayList<>();
		for (int i = SMALL_START; i <= SMALL_END; i++) {
			if (result.get(i) != 0) {
				small.add(result.get(i));
			}
		}
		FileWriter fwSmall = new FileWriter(new File(FILE_OUT_PATH + "src_churn_small.csv"));
		fwSmall.write("small");
		for (int i = 0; i < small.size(); i++) {
			fwSmall.write("\n" + small.get(i));
		}
		fwSmall.close();
		
		List<Double> medium = new ArrayList<>();
		for (int i = MEDIUM_START; i <= MEDIUM_END; i++) {
			if (result.get(i) != 0) {
				medium.add(result.get(i));
			}
		}
		FileWriter fwMedium = new FileWriter(new File(FILE_OUT_PATH + "src_churn_medium.csv"));
		fwMedium.write("medium");
		for (int i = 0; i < medium.size(); i++) {
			fwMedium.write("\n" + medium.get(i));
		}
		fwMedium.close();
		
		List<Double> large = new ArrayList<>();
		for (int i = LARGE_START; i <= LARGE_END; i++) {
			if (result.get(i) != 0) {
				large.add(result.get(i));
			}
		}
		FileWriter fwLarge = new FileWriter(new File(FILE_OUT_PATH + "src_churn_large.csv"));
		fwLarge.write("large");
		for (int i = 0; i < large.size(); i++) {
			fwLarge.write("\n" + large.get(i));
		}
		fwLarge.close();
		
		System.out.println("Total number of records used analyzing git_diff_src_churn: " + totalCt);
	}
	
	/**
	 * Analyze on git_diff_test_churn
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void testChurn() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Double>> list = new ArrayList<>();
		// team size per record is from 0 to 288
		for (int i = 0; i <= 288; i++) {
			List<Double> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	int curSize = cur.gh_team_size;
	        	if (prev != null &&
	        			cur.gh_team_size == prev.gh_team_size &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		// add an entry to the corresponding team size entry list
	        		// each entry is the test churn per hour
	        		list.get(curSize).add(
	        				(double)(cur.git_diff_test_churn * 3600)/secDiff(
	        						prev.gh_build_started_at, cur.gh_build_started_at));
	        		totalCt++;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		// calculate the average of entries for each team size
		List<Double> result = new ArrayList<>();
		for (int i = 0; i <= 288; i++) {
			result.add(avg(list.get(i)));
		}
		
		// generate csv files for all team sizes and for different team categories
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "test_churn.csv"));
		fw.write("entry,team_size");
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != 0) {
				fw.write("\n" + result.get(i) + "," + i);
			}
		}
		fw.close();
		
		List<Double> mini = new ArrayList<>();
		for (int i = MINI_START; i <= MINI_END; i++) {
			if (result.get(i) != 0) {
				mini.add(result.get(i));
			}
		}
		FileWriter fwMini = new FileWriter(new File(FILE_OUT_PATH + "test_churn_mini.csv"));
		fwMini.write("mini");
		for (int i = 0; i < mini.size(); i++) {
			fwMini.write("\n" + mini.get(i));
		}
		fwMini.close();
		
		List<Double> small = new ArrayList<>();
		for (int i = SMALL_START; i <= SMALL_END; i++) {
			if (result.get(i) != 0) {
				small.add(result.get(i));
			}
		}
		FileWriter fwSmall = new FileWriter(new File(FILE_OUT_PATH + "test_churn_small.csv"));
		fwSmall.write("small");
		for (int i = 0; i < small.size(); i++) {
			fwSmall.write("\n" + small.get(i));
		}
		fwSmall.close();
		
		List<Double> medium = new ArrayList<>();
		for (int i = MEDIUM_START; i <= MEDIUM_END; i++) {
			if (result.get(i) != 0) {
				medium.add(result.get(i));
			}
		}
		FileWriter fwMedium = new FileWriter(new File(FILE_OUT_PATH + "test_churn_medium.csv"));
		fwMedium.write("medium");
		for (int i = 0; i < medium.size(); i++) {
			fwMedium.write("\n" + medium.get(i));
		}
		fwMedium.close();
		
		List<Double> large = new ArrayList<>();
		for (int i = LARGE_START; i <= LARGE_END; i++) {
			if (result.get(i) != 0) {
				large.add(result.get(i));
			}
		}
		FileWriter fwLarge = new FileWriter(new File(FILE_OUT_PATH + "test_churn_large.csv"));
		fwLarge.write("large");
		for (int i = 0; i < large.size(); i++) {
			fwLarge.write("\n" + large.get(i));
		}
		fwLarge.close();
		
		System.out.println("Total number of records used analyzing git_diff_test_churn: " + totalCt);
	}
	
	/**
	 * Analyze on gh_sloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void sloc() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Double>> list = new ArrayList<>();
		// team size per record is from 0 to 288
		for (int i = 0; i <= 288; i++) {
			List<Double> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	int curSize = cur.gh_team_size;
	        	if (prev != null &&
	        			cur.gh_team_size == prev.gh_team_size &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0 &&
	        			cur.gh_sloc >= prev.gh_sloc) {
	        		// add an entry to the corresponding team size entry list
	        		// each entry is the sloc added per hour
	        		list.get(curSize).add(
	        				(double)((cur.gh_sloc - prev.gh_sloc) * 3600)/secDiff(
	        						prev.gh_build_started_at, cur.gh_build_started_at));
	        		totalCt++;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		// calculate the average of entries for each team size
		List<Double> result = new ArrayList<>();
		for (int i = 0; i <= 288; i++) {
			result.add(avg(list.get(i)));
		}
		
		// generate csv files for all team sizes and for different team categories
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "sloc.csv"));
		fw.write("entry,team_size");
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != 0) {
				fw.write("\n" + result.get(i) + "," + i);
			}
		}
		fw.close();
		
		List<Double> mini = new ArrayList<>();
		for (int i = MINI_START; i <= MINI_END; i++) {
			if (result.get(i) != 0) {
				mini.add(result.get(i));
			}
		}
		FileWriter fwMini = new FileWriter(new File(FILE_OUT_PATH + "sloc_mini.csv"));
		fwMini.write("mini");
		for (int i = 0; i < mini.size(); i++) {
			fwMini.write("\n" + mini.get(i));
		}
		fwMini.close();
		
		List<Double> small = new ArrayList<>();
		for (int i = SMALL_START; i <= SMALL_END; i++) {
			if (result.get(i) != 0) {
				small.add(result.get(i));
			}
		}
		FileWriter fwSmall = new FileWriter(new File(FILE_OUT_PATH + "sloc_small.csv"));
		fwSmall.write("small");
		for (int i = 0; i < small.size(); i++) {
			fwSmall.write("\n" + small.get(i));
		}
		fwSmall.close();
		
		List<Double> medium = new ArrayList<>();
		for (int i = MEDIUM_START; i <= MEDIUM_END; i++) {
			if (result.get(i) != 0) {
				medium.add(result.get(i));
			}
		}
		FileWriter fwMedium = new FileWriter(new File(FILE_OUT_PATH + "sloc_medium.csv"));
		fwMedium.write("medium");
		for (int i = 0; i < medium.size(); i++) {
			fwMedium.write("\n" + medium.get(i));
		}
		fwMedium.close();
		
		List<Double> large = new ArrayList<>();
		for (int i = LARGE_START; i <= LARGE_END; i++) {
			if (result.get(i) != 0) {
				large.add(result.get(i));
			}
		}
		FileWriter fwLarge = new FileWriter(new File(FILE_OUT_PATH + "sloc_large.csv"));
		fwLarge.write("large");
		for (int i = 0; i < large.size(); i++) {
			fwLarge.write("\n" + large.get(i));
		}
		fwLarge.close();
		
		System.out.println("Total number of records used analyzing gh_sloc: " + totalCt);
	}
	
	/**
	 * Analyze on gh_test_lines_per_kloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void testLinesDens() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Double>> list = new ArrayList<>();
		// team size per record is from 0 to 288
		for (int i = 0; i <= 288; i++) {
			List<Double> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	int curSize = cur.gh_team_size;
	        	if (prev != null &&
	        			cur.gh_team_size == prev.gh_team_size &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		// add an entry to the corresponding team size entry list
	        		// each entry is the gh_test_lines_per_kloc
	        		list.get(curSize).add(cur.gh_test_lines_per_kloc);
	        		totalCt++;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		// calculate the average of entries for each team size
		List<Double> result = new ArrayList<>();
		for (int i = 0; i <= 288; i++) {
			result.add(avg(list.get(i)));
		}
		
		// generate csv files for all team sizes and for different team categories
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "test_lines_dens.csv"));
		fw.write("entry,team_size");
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != 0) {
				fw.write("\n" + result.get(i) + "," + i);
			}
		}
		fw.close();
		
		List<Double> mini = new ArrayList<>();
		for (int i = MINI_START; i <= MINI_END; i++) {
			if (result.get(i) != 0) {
				mini.add(result.get(i));
			}
		}
		FileWriter fwMini = new FileWriter(new File(FILE_OUT_PATH + "test_lines_dens_mini.csv"));
		fwMini.write("mini");
		for (int i = 0; i < mini.size(); i++) {
			fwMini.write("\n" + mini.get(i));
		}
		fwMini.close();
		
		List<Double> small = new ArrayList<>();
		for (int i = SMALL_START; i <= SMALL_END; i++) {
			if (result.get(i) != 0) {
				small.add(result.get(i));
			}
		}
		FileWriter fwSmall = new FileWriter(new File(FILE_OUT_PATH + "test_lines_dens_small.csv"));
		fwSmall.write("small");
		for (int i = 0; i < small.size(); i++) {
			fwSmall.write("\n" + small.get(i));
		}
		fwSmall.close();
		
		List<Double> medium = new ArrayList<>();
		for (int i = MEDIUM_START; i <= MEDIUM_END; i++) {
			if (result.get(i) != 0) {
				medium.add(result.get(i));
			}
		}
		FileWriter fwMedium = new FileWriter(new File(FILE_OUT_PATH + "test_lines_dens_medium.csv"));
		fwMedium.write("medium");
		for (int i = 0; i < medium.size(); i++) {
			fwMedium.write("\n" + medium.get(i));
		}
		fwMedium.close();
		
		List<Double> large = new ArrayList<>();
		for (int i = LARGE_START; i <= LARGE_END; i++) {
			if (result.get(i) != 0) {
				large.add(result.get(i));
			}
		}
		FileWriter fwLarge = new FileWriter(new File(FILE_OUT_PATH + "test_lines_dens_large.csv"));
		fwLarge.write("large");
		for (int i = 0; i < large.size(); i++) {
			fwLarge.write("\n" + large.get(i));
		}
		fwLarge.close();
		
		System.out.println("Total number of records used analyzing gh_test_lines_per_kloc: " + totalCt);
	}
	
	/**
	 * Analyze on gh_test_cases_per_kloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void testCasesDens() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Double>> list = new ArrayList<>();
		// team size per record is from 0 to 288
		for (int i = 0; i <= 288; i++) {
			List<Double> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	int curSize = cur.gh_team_size;
	        	if (prev != null &&
	        			cur.gh_team_size == prev.gh_team_size &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		// add an entry to the corresponding team size entry list
	        		// each entry is the gh_test_cases_per_kloc
	        		list.get(curSize).add(cur.gh_test_cases_per_kloc);
	        		totalCt++;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		// calculate the average of entries for each team size
		List<Double> result = new ArrayList<>();
		for (int i = 0; i <= 288; i++) {
			result.add(avg(list.get(i)));
		}
		
		// generate csv files for all team sizes and for different team categories
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "test_cases_dens.csv"));
		fw.write("entry,team_size");
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != 0) {
				fw.write("\n" + result.get(i) + "," + i);
			}
		}
		fw.close();
		
		List<Double> mini = new ArrayList<>();
		for (int i = MINI_START; i <= MINI_END; i++) {
			if (result.get(i) != 0) {
				mini.add(result.get(i));
			}
		}
		FileWriter fwMini = new FileWriter(new File(FILE_OUT_PATH + "test_cases_dens_mini.csv"));
		fwMini.write("mini");
		for (int i = 0; i < mini.size(); i++) {
			fwMini.write("\n" + mini.get(i));
		}
		fwMini.close();
		
		List<Double> small = new ArrayList<>();
		for (int i = SMALL_START; i <= SMALL_END; i++) {
			if (result.get(i) != 0) {
				small.add(result.get(i));
			}
		}
		FileWriter fwSmall = new FileWriter(new File(FILE_OUT_PATH + "test_cases_dens_small.csv"));
		fwSmall.write("small");
		for (int i = 0; i < small.size(); i++) {
			fwSmall.write("\n" + small.get(i));
		}
		fwSmall.close();
		
		List<Double> medium = new ArrayList<>();
		for (int i = MEDIUM_START; i <= MEDIUM_END; i++) {
			if (result.get(i) != 0) {
				medium.add(result.get(i));
			}
		}
		FileWriter fwMedium = new FileWriter(new File(FILE_OUT_PATH + "test_cases_dens_medium.csv"));
		fwMedium.write("medium");
		for (int i = 0; i < medium.size(); i++) {
			fwMedium.write("\n" + medium.get(i));
		}
		fwMedium.close();
		
		List<Double> large = new ArrayList<>();
		for (int i = LARGE_START; i <= LARGE_END; i++) {
			if (result.get(i) != 0) {
				large.add(result.get(i));
			}
		}
		FileWriter fwLarge = new FileWriter(new File(FILE_OUT_PATH + "test_cases_dens_large.csv"));
		fwLarge.write("large");
		for (int i = 0; i < large.size(); i++) {
			fwLarge.write("\n" + large.get(i));
		}
		fwLarge.close();
		
		System.out.println("Total number of records used analyzing gh_test_cases_per_kloc: " + totalCt);
	}
	
	/**
	 * Analyze on gh_asserts_cases_per_kloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void assertsCasesDens() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Double>> list = new ArrayList<>();
		// team size per record is from 0 to 288
		for (int i = 0; i <= 288; i++) {
			List<Double> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	int curSize = cur.gh_team_size;
	        	if (prev != null &&
	        			cur.gh_team_size == prev.gh_team_size &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		// add an entry to the corresponding team size entry list
	        		// each entry is the gh_asserts_cases_per_kloc
	        		list.get(curSize).add(cur.gh_asserts_cases_per_kloc);
	        		totalCt++;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		// calculate the average of entries for each team size
		List<Double> result = new ArrayList<>();
		for (int i = 0; i <= 288; i++) {
			result.add(avg(list.get(i)));
		}
		
		// generate csv files for all team sizes and for different team categories
		FileWriter fw = new FileWriter(new File(FILE_OUT_PATH + "asserts_cases_dens.csv"));
		fw.write("entry,team_size");
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != 0) {
				fw.write("\n" + result.get(i) + "," + i);
			}
		}
		fw.close();
		
		List<Double> mini = new ArrayList<>();
		for (int i = MINI_START; i <= MINI_END; i++) {
			if (result.get(i) != 0) {
				mini.add(result.get(i));
			}
		}
		FileWriter fwMini = new FileWriter(new File(FILE_OUT_PATH + "asserts_cases_dens_mini.csv"));
		fwMini.write("mini");
		for (int i = 0; i < mini.size(); i++) {
			fwMini.write("\n" + mini.get(i));
		}
		fwMini.close();
		
		List<Double> small = new ArrayList<>();
		for (int i = SMALL_START; i <= SMALL_END; i++) {
			if (result.get(i) != 0) {
				small.add(result.get(i));
			}
		}
		FileWriter fwSmall = new FileWriter(new File(FILE_OUT_PATH + "asserts_cases_dens_small.csv"));
		fwSmall.write("small");
		for (int i = 0; i < small.size(); i++) {
			fwSmall.write("\n" + small.get(i));
		}
		fwSmall.close();
		
		List<Double> medium = new ArrayList<>();
		for (int i = MEDIUM_START; i <= MEDIUM_END; i++) {
			if (result.get(i) != 0) {
				medium.add(result.get(i));
			}
		}
		FileWriter fwMedium = new FileWriter(new File(FILE_OUT_PATH + "asserts_cases_dens_medium.csv"));
		fwMedium.write("medium");
		for (int i = 0; i < medium.size(); i++) {
			fwMedium.write("\n" + medium.get(i));
		}
		fwMedium.close();
		
		List<Double> large = new ArrayList<>();
		for (int i = LARGE_START; i <= LARGE_END; i++) {
			if (result.get(i) != 0) {
				large.add(result.get(i));
			}
		}
		FileWriter fwLarge = new FileWriter(new File(FILE_OUT_PATH + "asserts_cases_dens_large.csv"));
		fwLarge.write("large");
		for (int i = 0; i < large.size(); i++) {
			fwLarge.write("\n" + large.get(i));
		}
		fwLarge.close();
		
		System.out.println("Total number of records used analyzing gh_asserts_cases_per_kloc: " + totalCt);
	}
	
	/**
	 * Analyze team change on git_num_all_built_commits
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void numCommitsTeamChange() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Boolean>> list = new ArrayList<>();
		// 0 - mini; 1 - small; 2 - medium; 3 - large
		for (int i = 0; i < 4; i++) {
			// true - affected; false - not affected
			List<Boolean> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			int prevSize = 0;
			List<Double> prevList = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	if (prev != null &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		Double tempEntry = (double)(cur.git_num_all_built_commits * 3600)/secDiff(
    						prev.gh_build_started_at, cur.gh_build_started_at);
	        		if (!prevList.isEmpty() && prevSize != cur.gh_team_size) {
	        			Double prevAvg = avg(prevList);
	        			list.get(getTeamCategory(prevSize)).add(tempEntry < prevAvg);
	        			prevList.clear();
	        			totalCt++;
	        		}
	        		prevList.add(tempEntry);
        			prevSize = cur.gh_team_size;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		System.out.println("Team change effects on git_num_all_built_commits:");
		int totalAffected = 0;
		int total = 0;
		for (int i = 0; i < 4; i++) {
			int subTotalAffected = 0;
			int subTotal = list.get(i).size();
			for (int j = 0; j < subTotal; j++) {
				if (list.get(i).get(j)) subTotalAffected++;
			}
			System.out.println(String.format("Team category %d: affected: %d, total: %d, percentage: %.5f",
					i, subTotalAffected, subTotal, (double)subTotalAffected/subTotal));
			totalAffected += subTotalAffected;
			total += subTotal;
		}
		
		System.out.println(String.format("Total: affected: %d, total: %d, percentage: %.5f\n",
				totalAffected, total, (double)totalAffected/total));
	}
	
	/**
	 * Analyze team change on git_diff_src_churn
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void srcChurnTeamChange() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Boolean>> list = new ArrayList<>();
		// 0 - mini; 1 - small; 2 - medium; 3 - large
		for (int i = 0; i < 4; i++) {
			// true - affected; false - not affected
			List<Boolean> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			int prevSize = 0;
			List<Double> prevList = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	if (prev != null &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		Double tempEntry = (double)(cur.git_diff_src_churn * 3600)/secDiff(
    						prev.gh_build_started_at, cur.gh_build_started_at);
	        		if (!prevList.isEmpty() && prevSize != cur.gh_team_size) {
	        			Double prevAvg = avg(prevList);
	        			list.get(getTeamCategory(prevSize)).add(tempEntry < prevAvg);
	        			prevList.clear();
	        			totalCt++;
	        		}
	        		prevList.add(tempEntry);
        			prevSize = cur.gh_team_size;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		System.out.println("Team change effects on git_diff_src_churn:");
		int totalAffected = 0;
		int total = 0;
		for (int i = 0; i < 4; i++) {
			int subTotalAffected = 0;
			int subTotal = list.get(i).size();
			for (int j = 0; j < subTotal; j++) {
				if (list.get(i).get(j)) subTotalAffected++;
			}
			System.out.println(String.format("Team category %d: affected: %d, total: %d, percentage: %.5f",
					i, subTotalAffected, subTotal, (double)subTotalAffected/subTotal));
			totalAffected += subTotalAffected;
			total += subTotal;
		}
		
		System.out.println(String.format("Total: affected: %d, total: %d, percentage: %.5f\n",
				totalAffected, total, (double)totalAffected/total));
	}
	
	/**
	 * Analyze team change on git_diff_test_churn
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void testChurnTeamChange() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Boolean>> list = new ArrayList<>();
		// 0 - mini; 1 - small; 2 - medium; 3 - large
		for (int i = 0; i < 4; i++) {
			// true - affected; false - not affected
			List<Boolean> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			int prevSize = 0;
			List<Double> prevList = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	if (prev != null &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		Double tempEntry = (double)(cur.git_diff_test_churn * 3600)/secDiff(
    						prev.gh_build_started_at, cur.gh_build_started_at);
	        		if (!prevList.isEmpty() && prevSize != cur.gh_team_size) {
	        			Double prevAvg = avg(prevList);
	        			list.get(getTeamCategory(prevSize)).add(tempEntry < prevAvg);
	        			prevList.clear();
	        			totalCt++;
	        		}
	        		prevList.add(tempEntry);
        			prevSize = cur.gh_team_size;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		System.out.println("Team change effects on git_diff_test_churn:");
		int totalAffected = 0;
		int total = 0;
		for (int i = 0; i < 4; i++) {
			int subTotalAffected = 0;
			int subTotal = list.get(i).size();
			for (int j = 0; j < subTotal; j++) {
				if (list.get(i).get(j)) subTotalAffected++;
			}
			System.out.println(String.format("Team category %d: affected: %d, total: %d, percentage: %.5f",
					i, subTotalAffected, subTotal, (double)subTotalAffected/subTotal));
			totalAffected += subTotalAffected;
			total += subTotal;
		}
		
		System.out.println(String.format("Total: affected: %d, total: %d, percentage: %.5f\n",
				totalAffected, total, (double)totalAffected/total));
	}
	
	/**
	 * Analyze team change on gh_sloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void slocTeamChange() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Boolean>> list = new ArrayList<>();
		// 0 - mini; 1 - small; 2 - medium; 3 - large
		for (int i = 0; i < 4; i++) {
			// true - affected; false - not affected
			List<Boolean> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			int prevSize = 0;
			List<Double> prevList = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	if (prev != null &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0 &&
	        			cur.gh_sloc >= prev.gh_sloc) {
	        		Double tempEntry = (double)((cur.gh_sloc - prev.gh_sloc) * 3600)/secDiff(
    						prev.gh_build_started_at, cur.gh_build_started_at);
	        		if (!prevList.isEmpty() && prevSize != cur.gh_team_size) {
	        			Double prevAvg = avg(prevList);
	        			list.get(getTeamCategory(prevSize)).add(tempEntry < prevAvg);
	        			prevList.clear();
	        			totalCt++;
	        		}
	        		prevList.add(tempEntry);
        			prevSize = cur.gh_team_size;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		System.out.println("Team change effects on gh_sloc:");
		int totalAffected = 0;
		int total = 0;
		for (int i = 0; i < 4; i++) {
			int subTotalAffected = 0;
			int subTotal = list.get(i).size();
			for (int j = 0; j < subTotal; j++) {
				if (list.get(i).get(j)) subTotalAffected++;
			}
			System.out.println(String.format("Team category %d: affected: %d, total: %d, percentage: %.5f",
					i, subTotalAffected, subTotal, (double)subTotalAffected/subTotal));
			totalAffected += subTotalAffected;
			total += subTotal;
		}
		
		System.out.println(String.format("Total: affected: %d, total: %d, percentage: %.5f\n",
				totalAffected, total, (double)totalAffected/total));
	}
	
	/**
	 * Analyze team change on gh_test_lines_per_kloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void testLinesDensTeamChange() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Boolean>> list = new ArrayList<>();
		// 0 - mini; 1 - small; 2 - medium; 3 - large
		for (int i = 0; i < 4; i++) {
			// true - affected; false - not affected
			List<Boolean> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			int prevSize = 0;
			List<Double> prevList = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	if (prev != null &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		Double tempEntry = cur.gh_test_lines_per_kloc;
	        		if (!prevList.isEmpty() && prevSize != cur.gh_team_size) {
	        			Double prevAvg = avg(prevList);
	        			list.get(getTeamCategory(prevSize)).add(tempEntry < prevAvg);
	        			prevList.clear();
	        			totalCt++;
	        		}
	        		prevList.add(tempEntry);
        			prevSize = cur.gh_team_size;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		System.out.println("Team change effects on gh_test_lines_per_kloc:");
		int totalAffected = 0;
		int total = 0;
		for (int i = 0; i < 4; i++) {
			int subTotalAffected = 0;
			int subTotal = list.get(i).size();
			for (int j = 0; j < subTotal; j++) {
				if (list.get(i).get(j)) subTotalAffected++;
			}
			System.out.println(String.format("Team category %d: affected: %d, total: %d, percentage: %.5f",
					i, subTotalAffected, subTotal, (double)subTotalAffected/subTotal));
			totalAffected += subTotalAffected;
			total += subTotal;
		}
		
		System.out.println(String.format("Total: affected: %d, total: %d, percentage: %.5f\n",
				totalAffected, total, (double)totalAffected/total));
	}
	
	/**
	 * Analyze team change on gh_test_cases_per_kloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void testCasesDensTeamChange() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Boolean>> list = new ArrayList<>();
		// 0 - mini; 1 - small; 2 - medium; 3 - large
		for (int i = 0; i < 4; i++) {
			// true - affected; false - not affected
			List<Boolean> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			int prevSize = 0;
			List<Double> prevList = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	if (prev != null &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		Double tempEntry = cur.gh_test_cases_per_kloc;
	        		if (!prevList.isEmpty() && prevSize != cur.gh_team_size) {
	        			Double prevAvg = avg(prevList);
	        			list.get(getTeamCategory(prevSize)).add(tempEntry < prevAvg);
	        			prevList.clear();
	        			totalCt++;
	        		}
	        		prevList.add(tempEntry);
        			prevSize = cur.gh_team_size;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		System.out.println("Team change effects on gh_test_cases_per_kloc:");
		int totalAffected = 0;
		int total = 0;
		for (int i = 0; i < 4; i++) {
			int subTotalAffected = 0;
			int subTotal = list.get(i).size();
			for (int j = 0; j < subTotal; j++) {
				if (list.get(i).get(j)) subTotalAffected++;
			}
			System.out.println(String.format("Team category %d: affected: %d, total: %d, percentage: %.5f",
					i, subTotalAffected, subTotal, (double)subTotalAffected/subTotal));
			totalAffected += subTotalAffected;
			total += subTotal;
		}
		
		System.out.println(String.format("Total: affected: %d, total: %d, percentage: %.5f\n",
				totalAffected, total, (double)totalAffected/total));
	}
	
	/**
	 * Analyze team change on gh_asserts_cases_per_kloc
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void assertsCasesDensTeamChange() throws IOException, ParseException {
		int totalCt = 0;
		List<List<Boolean>> list = new ArrayList<>();
		// 0 - mini; 1 - small; 2 - medium; 3 - large
		for (int i = 0; i < 4; i++) {
			// true - affected; false - not affected
			List<Boolean> tempList = new ArrayList<>();
			list.add(tempList);
		}
		
		File[] files = new File(FILE_PATH).listFiles();
		for (File file : files) {
			int lineCt = 0;
			Record prev = null;
			int prevSize = 0;
			List<Double> prevList = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
	        String line = "";
	        while((line = br.readLine()) != null) {
	        	lineCt++;
	        	if (lineCt == 1) continue;
	        	
	        	Record cur = new Record(line);
	        	if (prev != null &&
	        			cur.gh_build_started_at.compareTo(prev.gh_build_started_at) > 0) {
	        		Double tempEntry = cur.gh_asserts_cases_per_kloc;
	        		if (!prevList.isEmpty() && prevSize != cur.gh_team_size) {
	        			Double prevAvg = avg(prevList);
	        			list.get(getTeamCategory(prevSize)).add(tempEntry < prevAvg);
	        			prevList.clear();
	        			totalCt++;
	        		}
	        		prevList.add(tempEntry);
        			prevSize = cur.gh_team_size;
	        	}
	        	prev = cur;
	        }
	        // minus one for header line
	        lineCt--;
		}
		
		System.out.println("Team change effects on gh_asserts_cases_per_kloc:");
		int totalAffected = 0;
		int total = 0;
		for (int i = 0; i < 4; i++) {
			int subTotalAffected = 0;
			int subTotal = list.get(i).size();
			for (int j = 0; j < subTotal; j++) {
				if (list.get(i).get(j)) subTotalAffected++;
			}
			System.out.println(String.format("Team category %d: affected: %d, total: %d, percentage: %.5f",
					i, subTotalAffected, subTotal, (double)subTotalAffected/subTotal));
			totalAffected += subTotalAffected;
			total += subTotal;
		}
		
		System.out.println(String.format("Total: affected: %d, total: %d, percentage: %.5f\n",
				totalAffected, total, (double)totalAffected/total));
	}
}
