import java.io.*;
import java.util.*;
class wtable
{
	String word;
	int count;
}
public class Read {
	static wtable w[]=new wtable[1000];
	static int wcnt=0;
	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub
		Scanner s =new Scanner(System.in);
		System.out.println("Enter file path");
		String file=s.next();
		BufferedReader br=new BufferedReader(new FileReader(file));
		String line=null;
		while((line=br.readLine())!=null)
		{
			String words[]=line.split(" ");
			for(int i=0;i<words.length;i++)
			{
				int j;
				for(j=0;j<wcnt && w[wcnt]!=null;j++)
				{
					if(words[i].equalsIgnoreCase(w[j].word))
					{
						w[j].count++;
						wcnt++;
						break;
					}
				}
				if(j==wcnt)
				{
					w[wcnt]=new wtable();
					w[wcnt].word=words[i];
					w[wcnt].count++;
					wcnt++;
				}
			}
		}
		for(int i=0;i<wcnt;i++)
		{
			System.out.println(w[i].word+"\t"+w[i].count);
		}
		br.close();
	}
}
