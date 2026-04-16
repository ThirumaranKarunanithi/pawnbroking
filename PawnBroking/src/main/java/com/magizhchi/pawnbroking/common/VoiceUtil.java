/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

/**
 *
 * @author Tiru
 */
public class VoiceUtil {
    
    private static String path = System.getenv("MBROLA_HOME");
    
    /*public static synchronized void textToSpeech(String text) {
        //System.setProperty("mbrola.base", "C:\\Program Files (x86)");
        listAllVoices();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() ->{
            if(CommonConstants.IRAIVA_SOUND_ON) {
                VoiceManager voiceManager = VoiceManager.getInstance();
                Voice voice;//Creating object of Voice class
                voice = voiceManager.getVoice("alan");//Getting voice
                if (voice != null) {
                    voice.allocate();//Allocating Voice
                }
                try {
                    //voice.setRate(100);//Setting the rate of the voice
                    //voice.setPitch(50);//Setting the Pitch of the voice
                    //voice.setVolume(200);//Setting the volume of the voice
                    voice.speak(text);//Calling speak() method
                }
                catch(Exception e) {
                    Logger.getLogger(VoiceUtil.class.getName()).log(Level.SEVERE, null, e);
                }    
            }
        });
        executorService.shutdown();
    }

    public static void speechToText(OwnerMainScreenController parent) {
        
        Configuration config = new Configuration();
        config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        config.setDictionaryPath("file:E:\\speechtotext.dic");
        config.setLanguageModelPath("file:E:\\speechtotext.lm");        
        
        try {
            LiveSpeechRecognizer speechRecognizer = new LiveSpeechRecognizer(config);
            speechRecognizer.startRecognition(true);
            
            SpeechResult speechResult = null;
			
            while ((speechResult = speechRecognizer.getResult()) != null) {
                String voiceCommand = speechResult.getHypothesis();
                System.out.println("Voice Command is " + voiceCommand);
                //parent.txtBillNumber.setText(voiceCommand);
                //parent.txtBillNumber.positionCaret(parent.txtBillNumber.getText().length());
                
                /*ExecutorService executor1 = Executors.newSingleThreadExecutor();
                executor1.submit(() -> {
                        parent.txtBillNumberOnAction(null);
                });
                //parent.goldBillOpeningScreenWork(null, null, false);
                //System.out.println("called function..............");
            }            
        } catch (IOException ex) {
            Logger.getLogger(VoiceUtil.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public static void listAllVoices() {
        System.out.println();
        System.out.println("All voices available:");
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice[] voices = voiceManager.getVoices();
        for (int i = 0; i < voices.length; i++) {
            System.out.println("    " + voices[i].getName() + " (" + voices[i].getDomain() + " domain)");
        }
    }*/
}
