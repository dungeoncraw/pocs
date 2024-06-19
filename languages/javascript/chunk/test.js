import { Readable } from "stream";

const test = () => {
  const readable = Readable.from(Array.from({length: 50}, () => Math.floor(Math.random() * 100)));
  
  readable.on("data", (chunk) => {
    console.log(chunk);
  });
}
test();
