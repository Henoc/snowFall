package snowFall;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;
import static java.lang.Math.*;


//こっちでメインの計算処理をやる。それぞれのクラスに計算結果の座標などを代入して、描画処理はその他クラスのpaintMachineとMainに任せる
//よって使うオブジェクトはこっちで生成しまくる
public class ScheduledTask extends TimerTask {

	Main main;
	static Random rnd = new Random();
	public ScheduledTask(Main main){
		this.main=main;
	}

	static ArrayList<GameOver> gameOverList = new ArrayList<GameOver>();
	static ArrayList<SnowMatrix> snowMatrixList = new ArrayList<SnowMatrix>();		//SnowMatrixを生成した分だけ登録しておく

	static boolean msLeft=false;		//マウス左をプレスしたか
	static int msx=0,msy=0;			//そのときのカーソル座標
	static int score=0;
	static int hiscore=0;
	static int life=3;
	static int numOfSolved=0,numOfFallen=0;
	static double spst=0;
	static int cpst=0;
	static int comboCnt=0;

	static int cnt=0;					//run内のカウント


	@Override
	public void run() {

		if(Main.gameMode==0) gameSetUp();
		if(Main.gameMode==1 || Main.gameMode==2) gameRunning();



		//情報更新
		main.repaint();
		msLeft=false;
		cnt++;
		if(cnt==Integer.MAX_VALUE) cnt=1;
	}

	//タイトル画面
	public void gameSetUp(){
		if(cnt==0){		//タイトル画面にボタンをセット
			gameOverList.add(new GameOver("Start",Main.e_w,Main.h*3/5,1));
			gameOverList.add(new GameOver("End",Main.e_w,Main.h*3/5+48,0));
		}

		for(GameOver go : gameOverList)
			go.motionMachine();
	}

	//ゲームプレイ中にやること
	public void gameRunning(){
		//GameOverクラスのボタン、SnowMatrixは全て削除しておく
		if(cnt==0){
			gameOverList.clear();
			snowMatrixList.clear();
		}
		//SnowMatrixをタイミングよく落とすメソッド
		snowFaller();

		//他のクラスにまかせる計算処理など
		Snow.snowFieldCheckInit();				//SnowクラスのsnowFieldCheckを使うため初期化
		for(SnowMatrix s : snowMatrixList)			//すべてのSnowMatrixオブジェクトについて、計算処理をする
			s.motionMachine();				//SnowMatrixの方でクリック処理をする　あと完成したかどうか判定する
		destroyer(snowMatrixList);			//status=-1のオブジェクトは全て破棄
		comboDestroyer(Combo.comboList);	//deleteFlag=trueなら破棄


		//ゲームオーバー処理
		if(life==0) {
			Main.gameMode=2;
			GameOver.cnt=0;
			life=-1;
			hiscore = max(score,hiscore);

			gameOverList.add(new GameOver("Continue",100,100,1));
			gameOverList.add(new GameOver("End",100,148,0));

			Main.wav[Main.WAV_GAMEOVER].start();
		}

		//GameOverクラスのボタンオブジェクトの動作
		for(GameOver go : gameOverList)
			go.motionMachine();
	}

	//SnowMatrixをタイミングよくおとす
	public void snowFaller(){
		if(cnt%timingGenerator(cnt)==0 || snowMatrixList.isEmpty()){
			if(range(0,numOfFallen%50,7)){
				int ptn=rnd.nextInt(3);
				if(ptn==0) snowMatrixCreator(FORM_TRIANGLE,CENTER,1,0.4,50);
				if(ptn==1) snowMatrixCreator(FORM_SQUARE,CENTER,1,0.4,50);
				if(ptn==2) snowMatrixCreator(FORM_STAR,CENTER,1,0.4,50);
			}
			if(range(7,numOfFallen%50,20)){
				int ptn=rnd.nextInt(3);
				if(ptn==0){
					snowMatrixCreator(FORM_TRIANGLE,LEFT_OF_2,1,0.4,50);
					snowMatrixCreator(FORM_TRIANGLE,RIGHT_OF_2,1,0.4,50);
				}
				if(ptn==1) snowMatrixCreator(FORM_LINE,CENTER,2,0.4,50);
				if(ptn==2) snowMatrixCreator(FORM_BALL,CENTER,2,0.4,100);
			}
			if(range(20,numOfFallen%50,30)){
				int ptn=rnd.nextInt(3);
				if(ptn==0) snowMatrixCreator(FORM_LATTICE,LEFT_OF_2,2,0.4,100);
				if(ptn==1) snowMatrixCreator(FORM_LATTICE,RIGHT_OF_2,2,0.4,100);
				if(ptn==2) snowMatrixCreator(FORM_TAIL,CENTER,1,0.6,100);
			}
			if(range(30,numOfFallen%50,31)) snowMatrixCreator(FORM_LONGTAIL,CENTER,2,0.8,100);
			if(range(31,numOfFallen%50,44)){
				int ptn=rnd.nextInt(5);
				if(ptn==0){
					snowMatrixCreator(FORM_BALL,LEFT_OF_3,2,0.4,100);
					snowMatrixCreator(FORM_BALL,CENTER,2,0.5,100);
					snowMatrixCreator(FORM_BALL,RIGHT_OF_3,2,0.6,100);
				}
				if(ptn==1) snowMatrixCreator(FORM_TRAMPOLINE,CENTER,2,0.3,100);
				if(ptn==2){
					snowMatrixCreator(FORM_BALL,LEFT_OF_3,2,0.6,100);
					snowMatrixCreator(FORM_BALL,CENTER,2,0.5,100);
					snowMatrixCreator(FORM_BALL,RIGHT_OF_3,2,0.4,100);
				}
				if(ptn==3){
					snowMatrixCreator(FORM_BALL,LEFT_OF_3,2,0.45,100);
					snowMatrixCreator(FORM_BALL,CENTER,2,0.6,100);
					snowMatrixCreator(FORM_BALL,RIGHT_OF_3,2,0.45,100);
				}
				if(ptn==4) snowMatrixCreator(FORM_LINE,CENTER,3,0.5,70);
			}
			if(range(44,numOfFallen%50,45)) snowMatrixCreator(FORM_BALL,CENTER,5,0.4,100);
			if(range(45,numOfFallen%50,50)) snowMatrixCreator(FORM_LINE,CENTER,7,0.9+0.1*(numOfFallen-45),0);		//もう１回遊べるドン！！

			cnt-=cnt%timingGenerator(cnt);
			numOfFallen++;
			if(numOfFallen%50==0){		//50回で１周して、難易度がちょっと上がる
				spst+=0.1;
				cpst+=1;
			}
		}
	}

	//周期を出す関数
	private int timingGenerator(int n){
		return (int)(600-Math.max(0, 500-5000/Math.sqrt(Math.sqrt(n+4))));
	}

	//status=-1のオブジェクトは用済みなので破棄
	public void destroyer(ArrayList<SnowMatrix> list){
		for(int i=0;i<list.size();i++)
			if(list.get(i).status==-1){
				if(list.get(i).vanishAnim==0){
					for(Snow s : list.get(i).snowList)		//SnowクラスのallSnowListに登録されているSnowオブジェクトも消す
						Snow.allSnowList.remove(s);
					list.remove(i);
					i--;
				}
				else
					list.get(i).vanishAnim--;				//消す指定があってから消すまでにアニメーションの時間をとる
			}
	}

	//comboクラスのdestroyer
	public void comboDestroyer(ArrayList<Combo> list){
		for(int i=0;i<list.size();i++){
			if(list.get(i).deleteFlag){
				list.remove(i);
				i--;
			}
		}
	}

	//range関数
	public boolean range(int bottom,int n,int upper){
		return bottom<=n && n<upper;
	}

	//snowMatrixCreator用の定数
	static final int FORM_TRIANGLE=0;
	static final int FORM_SQUARE=1;
	static final int FORM_TRAMPOLINE=2;
	static final int FORM_LINE=3;
	static final int FORM_LATTICE=4;
	static final int FORM_TAIL=5;
	static final int FORM_LONGTAIL=6;
	static final int FORM_BALL=7;
	static final int FORM_STAR=8;

	static final int CENTER=Main.e_w/2;
	static final int CENTER_OF_3=Main.e_w/2;
	static final int LEFT_OF_3=Main.e_w/6;
	static final int RIGHT_OF_3=Main.e_w/6*5;
	static final int LEFT_OF_2=Main.e_w/4;
	static final int RIGHT_OF_2=Main.e_w/4*3;

	//SnowMatrixコンストラクタ用の定数
	static final int NORMAL_EDGE=0;
	static final int LINE_EDGE=1;
	static final int NO_EDGE=2;
	static final int LATTICE_EDGE=3;
	static final int CIRCLE_EDGE=4;

	//指定された形のSnowMatrixを追加する
	public void snowMatrixCreator(int form,int layout,int complex,double fspeed,int density){
		complex+=cpst;
		fspeed+=spst;

		if(form==FORM_TRIANGLE){
			int[] posx = new int[]{-50+layout,layout,50+layout};
			int[] posy = new int[]{88,18,88};
			snowMatrixList.add(new SnowMatrix(rnd,3,complex,posx,posy,fspeed,density,NORMAL_EDGE));
		}
		if(form==FORM_SQUARE){
			int[] posx = new int[]{-103+layout,-58+layout,52+layout,93+layout};
			int[] posy = new int[]{33,104,11,80};
			snowMatrixList.add(new SnowMatrix(rnd,4,complex,posx,posy,fspeed,density,CIRCLE_EDGE));
		}
		if(form==FORM_TRAMPOLINE){
			int[] posx = new int[]{-178+layout,-178+layout,0+layout,178+layout,178+layout};
			int[] posy = new int[]{28,92,0,28,92};
			snowMatrixList.add(new SnowMatrix(rnd,5,complex,posx,posy,fspeed,density,NORMAL_EDGE));
		}
		if(form==FORM_LINE){
			int[] posx = new int[]{-210+layout,-140+layout,-70+layout,layout,70+layout,140+layout,210+layout};
			int[] posy = new int[]{60,60,60,60,60,60,60};
			snowMatrixList.add(new SnowMatrix(rnd,7,complex,posx,posy,fspeed,density,LINE_EDGE));
		}
		if(form==FORM_LATTICE){
			int[] posx = new int[]{-53+layout,layout,53+layout,53+layout,53+layout,layout,-53+layout,-53+layout,layout};
			int[] posy = new int[]{20,20,20,60,100,100,100,60,60};
			snowMatrixList.add(new SnowMatrix(rnd,9,complex,posx,posy,fspeed,density,LATTICE_EDGE));
		}
		if(form==FORM_TAIL){
			int[] posx = new int[]{20+layout,-10+layout,5+layout,-3+layout,layout,layout,layout};
			int[] posy = new int[]{100,60,20,-20,-60,-100,-150};
			snowMatrixList.add(new SnowMatrix(rnd,7,complex,posx,posy,fspeed,density,LINE_EDGE));
		}
		if(form==FORM_LONGTAIL){
			int[] posx = new int[]{60+layout,-40+layout,25+layout,-10+layout,3+layout,-1+layout,layout,layout,layout,layout,layout,layout};
			int[] posy = new int[]{100,60,20,-20,-60,-100,-140,-170,-210,-240,-270,-300};
			snowMatrixList.add(new SnowMatrix(rnd,12,complex,posx,posy,fspeed,density,LINE_EDGE));
		}
		if(form==FORM_BALL){
			int[] posx = new int[]{layout,layout+28,layout+40,layout+28,layout,layout-28,layout-40,layout-28};
			int[] posy = new int[]{20,32,60,88,100,88,60,32};
			snowMatrixList.add(new SnowMatrix(rnd,8,complex,posx,posy,fspeed,density,CIRCLE_EDGE));
		}
		if(form==FORM_STAR){
			int[] posx = new int[]{layout+(int)(60*cos(Math.PI/2)),layout+(int)(60*cos(Math.PI*1.3)),layout+(int)(60*cos(Math.PI*2.1)),layout+(int)(60*cos(Math.PI*2.9)),layout+(int)(60*cos(Math.PI*3.7))};
			int[] posy = new int[]{(int)(60*sin(-Math.PI/2)),(int)(60*sin(-Math.PI*1.3)),(int)(60*sin(-Math.PI*2.1)),(int)(60*sin(-Math.PI*2.9)),(int)(60*sin(-Math.PI*3.7))};
			snowMatrixList.add(new SnowMatrix(rnd,5,complex,posx,posy,fspeed,density,CIRCLE_EDGE));
		}
	}

}
