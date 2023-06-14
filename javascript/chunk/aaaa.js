import { Readable } from "stream";

const myTest = () => {
  const readable = Readable.from(Array(8));
  
  readable.on("data", (chunk) => {
    console.log(chunk);
  });
}

export default myTest;
