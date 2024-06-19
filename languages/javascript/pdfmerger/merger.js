const PDFMerger = require('pdf-merger-js');
var fs = require('fs');

var merger = new PDFMerger();

var pdf1 = fs.readFileSync('./sample.pdf');
var pdf2 = fs.readFileSync('./sample1mb.pdf');


(async () => {
  merger.add(pdf1);  //merge all pages. parameter is the path to file and filename.
  merger.add(pdf2); // merge only page 2

  await merger.save('merged.pdf'); //save under given name and reset the internal document
})();
