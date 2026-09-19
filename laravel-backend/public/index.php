<?php

use Illuminate\Http\Request;

define('LARAVEL_START', microtime(true));

// Suppress deprecation warnings that break JSON responses
error_reporting(E_ALL & ~E_DEPRECATED);
ini_set('display_errors', '0');

// Determine if the application is in maintenance mode...
if (file_exists($maintenance = __DIR__.'/../storage/framework/maintenance.php')) {
    require $maintenance;
}

// Register the Auto Loader...
require __DIR__.'/../vendor/autoload.php';

// Bootstrap Laravel and handle the request...
(require_once __DIR__.'/../bootstrap/app.php')
    ->handleRequest(Request::capture());
