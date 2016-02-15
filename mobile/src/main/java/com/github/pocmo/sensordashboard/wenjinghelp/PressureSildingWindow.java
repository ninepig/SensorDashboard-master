package com.github.pocmo.sensordashboard.wenjinghelp;

/**
 * Created by y on 15/12/16.
 */
public class PressureSildingWindow {

    public synchronized double[]  getPressureArray() {
        return pressureArray;
    }

    public void setPressureArray(double[] pressureArray) {
        this.pressureArray = pressureArray;
    }

    private double[] pressureArray;

    private int arrayLength = 40;
    private int count = 0;


    public PressureSildingWindow(){
        pressureArray = new double[arrayLength];

    }


    public double getArrayAverage(){
        double averagePressure = 0;
        int averageNumber = 0 ;
        double sum= 0;
        for(int i =0 ;i <arrayLength;i++){
            if(pressureArray[i]==0){
                break;
            }
            sum += pressureArray[i];
            averageNumber++;
        }
        averagePressure = sum/averageNumber;
        return averagePressure;
    }

    public double getLargestOne(){
        double largestOne = 0;

        for(int i = 0; i<arrayLength;i++){
            if(pressureArray[i]>largestOne){
                largestOne = pressureArray[i];
            }
        }

        return largestOne;
    }

    public double getSmallestOne(){
        double smallestOne =pressureArray[0];

        for(int i = 0; i<arrayLength;i++){
            if(pressureArray[i]==0){
                break;
            }
            if(pressureArray[i]<smallestOne){
                smallestOne = pressureArray[i];
            }
        }

        return smallestOne;
    }


    public void addNumber(double thisPressure){

        pressureArray[count%arrayLength] = thisPressure;
        count++;
    }



}