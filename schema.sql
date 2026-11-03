-- ============================================================================
-- FarmTrace: Smart Farm-to-Table Tracking System
-- PostgreSQL / Supabase Schema & Row-Level Security (RLS) Configuration
-- ============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------------------------------------------------------
-- Table 1: profiles
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    display_name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'farmer', -- 'farmer', 'logistics', 'quality_inspector', 'admin'
    contact_email TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- RLS for profiles
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public profiles are viewable by everyone" 
ON public.profiles FOR SELECT USING (true);

CREATE POLICY "Users can update their own profile" 
ON public.profiles FOR UPDATE USING (auth.uid() = id);

-- ----------------------------------------------------------------------------
-- Table 2: batches
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.batches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    batch_code TEXT UNIQUE NOT NULL, -- e.g. FT-2026-000123
    owner_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    vegetable_name TEXT NOT NULL,
    variety TEXT,
    quantity NUMERIC(10, 2) NOT NULL CHECK (quantity > 0),
    unit TEXT NOT NULL DEFAULT 'kg',
    farm_name TEXT NOT NULL,
    farm_location TEXT NOT NULL,
    harvest_date DATE NOT NULL,
    organic_certified BOOLEAN NOT NULL DEFAULT true,
    quality_grade TEXT DEFAULT 'Grade A Premium',
    description TEXT,
    qr_payload_url TEXT NOT NULL,
    current_status TEXT NOT NULL DEFAULT 'REGISTERED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_batches_code ON public.batches(batch_code);
CREATE INDEX IF NOT EXISTS idx_batches_owner ON public.batches(owner_id);

-- RLS for batches
ALTER TABLE public.batches ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public shoppers can view batch records" 
ON public.batches FOR SELECT USING (true);

CREATE POLICY "Farmers can insert their own batches" 
ON public.batches FOR INSERT WITH CHECK (auth.uid() = owner_id OR auth.uid() IS NOT NULL);

CREATE POLICY "Farmers can update their own batches" 
ON public.batches FOR UPDATE USING (auth.uid() = owner_id);

-- ----------------------------------------------------------------------------
-- Table 3: journey_events
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.journey_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    batch_id UUID NOT NULL REFERENCES public.batches(id) ON DELETE CASCADE,
    event_type TEXT NOT NULL, -- 'HARVESTED', 'RECEIVED_AT_MARKET', 'COLD_CHAIN_TRANSIT', 'SHELVED_FOR_SALE', etc.
    stage TEXT NOT NULL,      -- 'FARM', 'WHOLESALE_MARKET', 'TRANSPORTATION', 'RETAIL_SHOP', 'CONSUMER_KITCHEN'
    location_name TEXT NOT NULL,
    city_region TEXT NOT NULL,
    latitude NUMERIC(10, 6),
    longitude NUMERIC(10, 6),
    distance_from_previous_km NUMERIC(8, 2) DEFAULT 0.0,
    handler_role TEXT NOT NULL,
    description TEXT NOT NULL,
    temperature_c NUMERIC(5, 2),
    humidity_percent INTEGER,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES public.profiles(id)
);

CREATE INDEX IF NOT EXISTS idx_journey_events_batch ON public.journey_events(batch_id, recorded_at ASC);

-- RLS for journey_events
ALTER TABLE public.journey_events ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public shoppers can read journey events" 
ON public.journey_events FOR SELECT USING (true);

CREATE POLICY "Authorized handlers can insert journey events" 
ON public.journey_events FOR INSERT WITH CHECK (auth.uid() IS NOT NULL);

-- ----------------------------------------------------------------------------
-- Table 4: batch_audit_logs
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.batch_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    batch_id UUID NOT NULL REFERENCES public.batches(id) ON DELETE CASCADE,
    actor_id UUID REFERENCES public.profiles(id),
    action TEXT NOT NULL,
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.batch_audit_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Audit logs can be read by authorized actors" 
ON public.batch_audit_logs FOR SELECT USING (auth.uid() = actor_id);
