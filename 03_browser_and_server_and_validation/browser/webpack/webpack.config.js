var path = require('path');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: './index.js',
    resolve: {
        modules: ['./node_modules', '../build/kotlin-javascript-dependencies', '../build/classes/main'].map(function (s) {
            return path.resolve(__dirname, s);
        })
    },
    output: {
        path: __dirname + "/build",
        filename: "bundle.js"
    },
    devServer: {
        hot: true,
        proxy: {
            "/api": {
                target: "http://localhost:5000",
                pathRewrite: {"^/api": ""}
            }
        }
    },
    plugins: [
        new HtmlWebpackPlugin({template: './index.ejs'}),
        new webpack.HotModuleReplacementPlugin()
    ]
};
