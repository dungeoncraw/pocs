import Sound from 'react-native-sound';

Sound.setCategory('Playback');

export default class SequentialAudioPlayer {
    private audioList: string[];
    private currentIndex: number = 0;
    private timeoutId: ReturnType<typeof setTimeout> | null = null;
    private isPlaying: boolean = false;

    constructor(audioList: string[]) {
        this.audioList = audioList;
    }

    public start() {
        if (this.audioList.length === 0) return;

        this.isPlaying = true;
        this.currentIndex = 0;
        this.playCurrentAudio();
    }

    private playCurrentAudio() {
        const filename = this.audioList[this.currentIndex];
        console.log(`Playing: ${filename}`);

        const sound = new Sound(filename, Sound.MAIN_BUNDLE, (error) => {
            if (error) {
                console.log(`Error playCurrentAudio ${filename}:`, error);
                this.scheduleNext();
                return;
            }

            sound.play((success) => {
                if (success) {
                    console.log(`Ended: ${filename}`);
                } else {
                    console.log(`Error on play: ${filename}`);
                }

                sound.release();
                this.scheduleNext();
            });
        });
    }

    private scheduleNext() {
        this.currentIndex += 1;

        if (this.currentIndex < this.audioList.length && this.isPlaying) {
            this.timeoutId = setTimeout(() => {
                this.playCurrentAudio();
            }, 10000);
        } else {
            console.log('All set.');
            this.isPlaying = false;
        }
    }

    public stop() {
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }
        this.isPlaying = false;
        this.currentIndex = 0;
        console.log('Stopped');
    }
}
